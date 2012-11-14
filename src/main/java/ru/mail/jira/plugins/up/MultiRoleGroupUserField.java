/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverterImpl;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.FieldVisibilityManager;

/**
 * Role group multi custom field.
 * 
 * @author Andrey Markelov
 */
public class MultiRoleGroupUserField
    extends MultiUserCFType
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Group manager.
     */
    private final GroupManager grMgr;

    /**
     * Project role manager.
     */
    private final ProjectRoleManager projectRoleManager;

    /**
     * Constructor.
     */
    public MultiRoleGroupUserField(
        CustomFieldValuePersister customFieldValuePersister,
        StringConverter stringConverter,
        GenericConfigManager genericConfigManager,
        ApplicationProperties applicationProperties,
        JiraAuthenticationContext authenticationContext,
        UserPickerSearchService searchService,
        FieldVisibilityManager fieldVisibilityManager,
        PluginData data,
        GroupManager grMgr,
        ProjectRoleManager projectRoleManager)
    {
        super(
            customFieldValuePersister,
            stringConverter,
            genericConfigManager,
            new MultiUserConverterImpl(ComponentManager.getInstance().getUserUtil()),
            applicationProperties,
            authenticationContext,
            searchService,
            fieldVisibilityManager);
        this.data = data;
        this.grMgr = grMgr;
        this.projectRoleManager = projectRoleManager;
    }

    private void fillDataLists(
        String shares_data,
        List<String> groups,
        List<ProjRole> projRoles)
    {
        if (shares_data == null || shares_data.length() == 0)
        {
            return;
        }

        try
        {
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
        catch (JSONException e)
        {
            log.error("AdRoleGroupUserCfService::fillLists - Incorrect field data", e);
            //--> impossible
        }
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    private Map<String, String> getProjectRoleUsers(
        String role,
        Project currProj)
    {
        Map<String, String> map = new HashMap<String, String>();

        if (role.equals(""))
        {
            Collection<ProjectRole> projRoles = projectRoleManager.getProjectRoles();
            for (ProjectRole pRole : projRoles)
            {
                ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(pRole, currProj);
                Set users = projectRoleActors.getUsers();
                for (Object obj : users)
                {
                    if (obj instanceof com.opensymphony.user.User)
                    {
                        com.opensymphony.user.User objUser = (com.opensymphony.user.User)obj;
                        map.put(objUser.getName(), objUser.getDisplayName());
                    }
                    else if (obj instanceof User)
                    {
                        User objUser = (User)obj;
                        map.put(objUser.getName(), objUser.getDisplayName());
                    }
                }
            }
        }
        else
        {
            ProjectRole projRole = projectRoleManager.getProjectRole(Long.valueOf(role));
            ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projRole, currProj);
            Set users = projectRoleActors.getUsers();
            for (Object obj : users)
            {
                if (obj instanceof com.opensymphony.user.User)
                {
                    com.opensymphony.user.User objUser = (com.opensymphony.user.User)obj;
                    map.put(objUser.getName(), objUser.getDisplayName());
                }
                else if (obj instanceof User)
                {
                    User objUser = (User)obj;
                    map.put(objUser.getName(), objUser.getDisplayName());
                }
            }
        }

        return map;
    }

    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field, FieldLayoutItem fieldLayoutItem)
    {
    	Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);

        Map<String, String> map = new HashMap<String, String>();

        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        fillDataLists(data.getRoleGroupFieldData(field.getId()), groups, projRoles);

        for (String group : groups)
        {
            Collection<User> users = grMgr.getUsersInGroup(group);
            if (users != null)
            {
                for (User user : users)
                {
                    map.put(user.getName(), user.getDisplayName());
                }
            }
        }

        for (ProjRole pr : projRoles)
        {
            Project proj = issue.getProjectObject();
            if (proj != null && proj.getId().toString().equals(pr.getProject()))
            {
                map.putAll(getProjectRoleUsers(pr.getRole(), proj));
            }
        }

        TreeMap<String, String> sorted_map = new TreeMap<String, String>(new ValueComparator(map));
        sorted_map.putAll(map);

        Object issueValObj = issue.getCustomFieldValue(field);
        if (issueValObj == null)
        {
            params.put("selectVal", "");
        }
        else
        {
            params.put("selectVal", Utils.removeBrackets(issueValObj.toString()));
        }

        params.put("map", sorted_map);
        List<String> issueVal = Utils.convertList(issueValObj);
        params.put("issueVal", issueVal);

        return params;
    }
}
