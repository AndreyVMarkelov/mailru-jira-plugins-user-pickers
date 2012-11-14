/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.ofbiz.core.entity.GenericValue;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * Plug-In configure page.
 * 
 * @author Andrey Markelov
 */
public class UserPickerAdminAction
    extends JiraWebActionSupport
{
    /**
     * Unique ID.
     */
    private static final long serialVersionUID = 8016730182922672388L;

    /**
     * Application properties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Custom field manager.
     */
    private final CustomFieldManager cfMgr;

    /**
     * Data manager.
     */
    private final PluginData data;

    /**
     * Multi fields.
     */
    private Map<String, FieldData> multiFields;

    /**
     * Project manager.
     */
    private final ProjectManager prMgr;

    /**
     * Project role manager.
     */
    private final ProjectRoleManager projectRoleManager;

    /**
     * Single fields.
     */
    private Map<String, FieldData> singleFields;

    /**
     * Constructor.
     */
    public UserPickerAdminAction(
        ApplicationProperties applicationProperties,
        CustomFieldManager cfMgr,
        ProjectManager prMgr,
        ProjectRoleManager projectRoleManager,
        PluginData data)
    {
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
        this.prMgr = prMgr;
        this.projectRoleManager = projectRoleManager;
        this.data = data;
        this.singleFields = new LinkedHashMap<String, FieldData>();
        this.multiFields = new LinkedHashMap<String, FieldData>();
    }

    @Override
    public String doDefault()
    throws Exception
    {
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList)
        {
            if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:single_role_group_usercf") ||
                cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:multi_role_group_usercf"))
            {
                FieldData fdata = new FieldData(cf.getId(), cf.getName());
                if (cf.isAllProjects())
                {
                    fdata.setAllProjects(true);
                }
                else
                {
                    fdata.setAllProjects(false);
                    List<String> fieldProjs = new ArrayList<String>();
                    List<GenericValue> projs = cf.getAssociatedProjects();
                    for (GenericValue proj : projs)
                    {
                        fieldProjs.add((String) proj.get("name"));
                    }

                    fdata.setProjects(fieldProjs);
                }

                List<String> groups = new ArrayList<String>();
                List<ProjRole> projRoles = new ArrayList<ProjRole>();
                fillDataLists(data.getRoleGroupFieldData(cf.getId()), groups, projRoles);
                fdata.addGroups(groups);
                fdata.addRoles(projRoles);

                if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:single_role_group_usercf"))
                {
                    singleFields.put(fdata.getFieldId(), fdata);
                }
                else
                {
                    multiFields.put(fdata.getFieldId(), fdata);
                }
            }
        }

        return SUCCESS;
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

    /**
     * Get context path.
     */
    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public Map<String, FieldData> getMultiFields()
    {
        return multiFields;
    }

    /**
     * Get all projects.
     */
    public Map<String, String> getProjectMap()
    {
        List<Project> projects = prMgr.getProjectObjects();
        Map<String, String> projs = new TreeMap<String, String>();
        if (projects != null)
        {
            for (Project project : projects)
            {
                projs.put(project.getId().toString(), project.getName());
            }
        }

        return projs;
    }

    /**
     * Get all roles.
     */
    public Map<String, String> getRoleMap()
    {
        Map<String, String> roleProjs = new TreeMap<String, String>();
        Collection<ProjectRole> roles = projectRoleManager.getProjectRoles();
        if (roles != null)
        {
            for (ProjectRole role : roles)
            {
                roleProjs.put(role.getId().toString(), role.getName());
            }
        }

        return roleProjs;
    }

    public Map<String, FieldData> getSingleFields()
    {
        return singleFields;
    }

    /**
     * Check administer permissions.
     */
    public boolean hasAdminPermission()
    {
        User user = getLoggedInUser();
        if (user == null)
        {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser()))
        {
            return true;
        }

        return false;
    }
}
