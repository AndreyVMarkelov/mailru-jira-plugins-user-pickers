/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
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
            Collection<User> users = (Collection<User>)obj;
            for (User user : users)
            {
                set.add(user.getName());
            }
        }
        else
        {
            String str = removeBrackets(obj.toString());
            StringTokenizer st = new StringTokenizer(str, ",");
            while (st.hasMoreTokens())
            {
                set.add(st.nextToken().trim());
            }
        }

        return set;
    }

    public static void fillDataLists(
        String shares_data,
        List<String> groups,
        List<ProjRole> projRoles)
    throws JSONException
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
                ProjRole pr = new ProjRole(obj.getString("proj"), obj.getString("role"));
                projRoles.add(pr);
            }
        }
    }

    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());
    }

    public static SortedSet<User> buildUsersList(GroupManager groupManager, ProjectRoleManager projectRoleManager,
                                                 Project project, List<String> groups, List<ProjRole> projRoles) {
        SortedSet<User> usersList = new TreeSet<User>(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
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
            if (project != null && project.getId().toString().equals(projRole.getProject()))
            {
                if (projRole.getRole().equals(""))
                {
                    Collection<ProjectRole> projectRoles = projectRoleManager.getProjectRoles();
                    for (ProjectRole projectRole : projectRoles)
                    {
                        ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRole, project);
                        usersList.addAll(projectRoleActors.getUsers());
                    }
                }
                else
                {
                    ProjectRole projectRole = projectRoleManager.getProjectRole(Long.valueOf(projRole.getRole()));
                    ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRole, project);
                    usersList.addAll(projectRoleActors.getUsers());
                }
            }
        }

        return usersList;
    }

    /**
     * Remove brackets from string.
     */
    public static String removeBrackets(String str)
    {
        if (str == null || str.length() == 0)
        {
            return "";
        }

        if (str.startsWith("["))
        {
            str = str.substring(1);
        }

        if (str.endsWith("]"))
        {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

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
    private Utils() {}
}
