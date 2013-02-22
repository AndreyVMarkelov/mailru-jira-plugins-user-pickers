/*
 * Created by Andrey Markelov 11-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins.up.common;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.mail.jira.plugins.up.structures.ProjRole;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.PluginAccessor;


/**
 * This class contains utility methods.
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    private static final String CF_RIGHTS_CLASS_NAME = "ru.mail.jira.plugins.settings.IMailRuCFRights";

    private static final String CF_RIGHTS_METHOD_CAN_EDIT_NAME = "canEdit";

    private static final String CF_RIGHTS_METHOD_CAN_VIEW_NAME = "canView";

    private static Object cfRightsInstance;

    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    
    private static UserProjectHistoryManager userProjectHistoryManager = ComponentManager
            .getComponentInstanceOfType(UserProjectHistoryManager.class);
    
    private static ProjectRoleManager roleManager = ComponentManager
            .getComponentInstanceOfType(ProjectRoleManager.class);

    /**
     * adds "canView" and "canEdit" keys to map
     */
    public static void addViewAndEditParameters(Map<String, Object> params,
        String cfId)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        User currentUser = authCtx.getLoggedInUser();
        Project currentProject = userProjectHistoryManager.getCurrentProject(
            Permissions.BROWSE, currentUser);

        boolean canEdit = Utils.canEditCF(currentUser, cfId, currentProject);
        params.put("canEdit", canEdit);
        if (canEdit)
        {
            params.put("canView", true);
        }
        else
        {
            params.put("canView",
                Utils.canViewCF(currentUser, cfId, currentProject));
        }
    }

    /**
     * Build user list.
     */
    public static SortedSet<User> buildUsersList(GroupManager groupManager,
        ProjectRoleManager projectRoleManager, Project project,
        List<String> groups, List<ProjRole> projRoles)
    {
        SortedSet<User> usersList = new TreeSet<User>(new Comparator<User>()
        {
            @Override
            public int compare(User o1, User o2)
            {
                return o1.getDisplayName().compareToIgnoreCase(
                    o2.getDisplayName());
            }
        });

        for (String group : groups)
        {
            Collection<User> users = groupManager.getUsersInGroup(group);
            if (users != null)
            {
                usersList.addAll(users);
            }
        }

        for (ProjRole projRole : projRoles)
        {
            if (project != null
                && project.getId().toString().equals(projRole.getProject()))
            {
                if (projRole.getRole().equals(""))
                {
                    Collection<ProjectRole> projectRoles = projectRoleManager
                        .getProjectRoles();
                    for (ProjectRole projectRole : projectRoles)
                    {
                        ProjectRoleActors projectRoleActors = projectRoleManager
                            .getProjectRoleActors(projectRole, project);
                        usersList.addAll(projectRoleActors.getUsers());
                    }
                }
                else
                {
                    ProjectRole projectRole = projectRoleManager
                        .getProjectRole(Long.valueOf(projRole.getRole()));
                    ProjectRoleActors projectRoleActors = projectRoleManager
                        .getProjectRoleActors(projectRole, project);
                    usersList.addAll(projectRoleActors.getUsers());
                }
            }
        }

        return usersList;
    }

    private static boolean canEditCF(User user, String cfId, Project project)
    {
        return getPermission(user, cfId, project,
            CF_RIGHTS_METHOD_CAN_EDIT_NAME, "canEditCF");
    }

    private static boolean canViewCF(User user, String cfId, Project project)
    {
        return getPermission(user, cfId, project,
            CF_RIGHTS_METHOD_CAN_VIEW_NAME, "canViewCF");
    }

    /**
     * Convert string list to java list.
     */
    public static Set<String> convertList(Object obj)
    {
        Set<String> set = new LinkedHashSet<String>();
        if (obj == null)
        {
            return set;
        }

        if (obj instanceof Collection<?>)
        {
            @SuppressWarnings("unchecked")
            Collection<User> users = (Collection<User>) obj;
            for (User user : users)
            {
                set.add(user.getName());
            }
        }
        else if (obj instanceof List<?>)
        {
            @SuppressWarnings("unchecked")
            List<User> users = (List<User>) obj;
            for (User user : users)
            {
                set.add(user.getName());
            }
        }

        return set;
    }

    public static String convertSetToString(Set<String> set)
    {
        StringBuilder result = new StringBuilder(Consts.EMPTY_STRING);
        if (set == null || set.size() <= 0)
        {
            return result.toString();
        }

        boolean isFirstPassed = false;
        for (String str : set)
        {
            if (isFirstPassed)
            {
                result.append(Consts.ELEMENTS_DIVIDER);
            }
            else
            {
                isFirstPassed = true;
            }
            result.append(str);
        }

        return result.toString();
    }

    public static Set<String> getAllUsers()
    {
        Collection<User> allUsers = ComponentAccessor.getUserUtil().getUsers();
        Set<String> result = new LinkedHashSet<String>(allUsers.size());
        for (User user : allUsers)
        {
            result.add(user.getName());
        }

        return result;
    }

    public static void fillDataLists(String shares_data, List<String> groups,
        List<ProjRole> projRoles, boolean isRestricted) throws JSONException
    {
        if (isRestricted)
        {
            if (shares_data == null || shares_data.length() == 0)
            {
                return;
            }

            JSONArray jsonObj = new JSONArray(shares_data);
            for (int i = 0; i < jsonObj.length(); i++)
            {
                JSONObject obj = jsonObj.getJSONObject(i);
                String type = obj.getString("type");
                if (type.equals("G"))
                {
                    groups.add(obj.getString("group"));
                }
                else
                {
                    ProjRole pr = new ProjRole(obj.getString("proj"),
                        obj.getString("role"));
                    projRoles.add(pr);
                }
            }
        }
        else
        {
            Collection<Group> allGroups = ComponentAccessor.getGroupManager()
                .getAllGroups();
            for (Group group : allGroups)
            {
                groups.add(group.getName());
            }

            JiraAuthenticationContext authCtx = ComponentAccessor
                .getJiraAuthenticationContext();
            User currentUser = authCtx.getLoggedInUser();
            Project currentProject = userProjectHistoryManager
                .getCurrentProject(Permissions.BROWSE, currentUser);

            Collection<ProjectRole> allRoles = roleManager.getProjectRoles();
            for (ProjectRole projectRole : allRoles)
            {
                ProjRole role = new ProjRole(String.valueOf(currentProject
                    .getId()), String.valueOf(projectRole.getId()));
                projRoles.add(role);
            }
        }
    }

    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":"
            + req.getServerPort() + req.getContextPath());
    }

    private static Object getCfRightsClass()
    {
        if (cfRightsInstance == null)
        {
            PluginAccessor pluginAccessor = ComponentManager.getInstance()
                .getPluginAccessor();
            Class<?> mailRuCfRightsClass;
            try
            {
                mailRuCfRightsClass = pluginAccessor.getClassLoader()
                    .loadClass(CF_RIGHTS_CLASS_NAME);
            }
            catch (ClassNotFoundException e)
            {
                log.info("Utils::getCfRightsClass - ClassNotfoundException "
                    + CF_RIGHTS_CLASS_NAME
                    + " not found. It is possible that plugin is turned off");
                return null;
            }
            cfRightsInstance = ComponentManager
                .getOSGiComponentInstanceOfType(mailRuCfRightsClass);
            if (cfRightsInstance == null)
            {
                log.info("Utils::getCfRightsClass - Class "
                    + CF_RIGHTS_CLASS_NAME
                    + ". Method getOSGiComponentInstanceOfType failed to load component");
            }
        }

        return cfRightsInstance;
    }

    private static String getErrorMessage(User user, String cfId,
        Project project, String internalMethodName, String externalMethodName,
        String exception)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Utils::");
        sb.append(internalMethodName);
        sb.append(" - Class ");
        sb.append(CF_RIGHTS_CLASS_NAME);
        sb.append(". ");
        sb.append(exception);
        sb.append(" occured invoking ");
        sb.append(externalMethodName);
        sb.append(" method ");

        return sb.toString();
    }

    private static boolean getPermission(User user, String cfId,
        Project project, String externalMethodName, String internalMethodName)
    {
        Object cfRights = getCfRightsClass();

        // occurs only if class not found or not load
        // it's possible that parent plugin was disabled manually
        // so we should return true
        if (cfRights == null)
        {
            return true;
        }

        Method[] methods = cfRights.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (externalMethodName.equals(methods[i].getName()))
            {
                Boolean result = Boolean.FALSE;
                try
                {
                    result = (Boolean) methods[i].invoke(cfRights, user, cfId,
                        project);
                }
                catch (IllegalArgumentException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "IllegalArgumentException"));
                }
                catch (IllegalAccessException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "IllegalAccessException"));
                }
                catch (InvocationTargetException e)
                {
                    log.error(getErrorMessage(user, cfId, project,
                        internalMethodName, externalMethodName,
                        "InvocationTargetException"));
                    cfRightsInstance = null;
                }

                return result;
            }
        }

        return false;
    }

    @SuppressWarnings({"rawtypes"})
    public static Map<String, String> getProjectRoleUsers(
        ProjectRoleManager projectRoleManager, String role, Project currProj)
    {
        Map<String, String> map = new HashMap<String, String>();

        if (role.equals(""))
        {
            Collection<ProjectRole> projRoles = projectRoleManager
                .getProjectRoles();
            for (ProjectRole pRole : projRoles)
            {
                ProjectRoleActors projectRoleActors = projectRoleManager
                    .getProjectRoleActors(pRole, currProj);
                Set users = projectRoleActors.getUsers();
                for (Object obj : users)
                {
                    if (obj instanceof User)
                    {
                        User objUser = (User) obj;
                        map.put(objUser.getName(), objUser.getDisplayName());
                    }
                }
            }
        }
        else
        {
            ProjectRole projRole = projectRoleManager.getProjectRole(Long
                .valueOf(role));
            ProjectRoleActors projectRoleActors = projectRoleManager
                .getProjectRoleActors(projRole, currProj);
            Set users = projectRoleActors.getUsers();
            for (Object obj : users)
            {
                if (obj instanceof User)
                {
                    User objUser = (User) obj;
                    map.put(objUser.getName(), objUser.getDisplayName());
                }
            }
        }

        return map;
    }

    public static boolean isOfGroupRoleUserPickerType(String cfType)
    {
        return Consts.CF_KEY_MULTI_USER_GR_ROLE_SELECT.equals(cfType)
            || Consts.CF_KEY_SINGLE_USER_GR_ROLE_SELECT.equals(cfType);
    }

    public static boolean isOfMultiUserType(String cfType)
    {
        return Consts.CF_KEY_MULTI_USER_GR_ROLE_SELECT.equals(cfType)
            || Consts.CF_KEY_MULTI_USER_SELECT.equals(cfType);
    }

    public static boolean isValidLongParam(String str)
    {
        boolean isValidLong = true;

        try
        {
            Long.valueOf(str);
        }
        catch (NumberFormatException e)
        {
            isValidLong = false;
        }
        return isValidLong;
    }

    public static boolean isValidStr(String str)
    {
        return (str != null && str.length() > 0);
    }

    /**
     * Convert set collection to string.
     */
    public static String setToStr(Set<String> set)
    {
        StringBuilder sb = new StringBuilder();
        if (set != null)
        {
            for (String s : set)
            {
                sb.append(s).append(",");
            }
        }

        return sb.toString();
    }

    /**
     * Private constructor.
     */
    private Utils()
    {
    }
}
