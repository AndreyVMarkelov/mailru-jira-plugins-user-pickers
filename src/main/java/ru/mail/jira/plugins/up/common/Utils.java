package ru.mail.jira.plugins.up.common;

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

/**
 * This class contains utility methods.
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    private static UserProjectHistoryManager userProjectHistoryManager = ComponentManager.getComponentInstanceOfType(UserProjectHistoryManager.class);

    private static ProjectRoleManager roleManager = ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class);

    public static void addViewAndEditParameters(Map<String, Object> params, String cfId) {
        params.put("canEdit", true);
        params.put("canView", true);
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
