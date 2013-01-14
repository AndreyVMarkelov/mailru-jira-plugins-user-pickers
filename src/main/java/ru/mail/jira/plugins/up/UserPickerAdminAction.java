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
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.util.json.JSONException;
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
     * Multi selected fields.
     */
    private Map<String, SelectedFieldData> multiSelectedFields;

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
     * Single selected fields.
     */
    private Map<String, SelectedFieldData> singleSelectedFields;

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
        this.singleSelectedFields = new LinkedHashMap<String, SelectedFieldData>();
        this.multiSelectedFields = new LinkedHashMap<String, SelectedFieldData>();
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
                    List<Project> projs = cf.getAssociatedProjectObjects();
                    for (Project proj : projs)
                    {
                        fieldProjs.add(proj.getName());
                    }

                    fdata.setProjects(fieldProjs);
                }

                List<String> groups = new ArrayList<String>();
                List<ProjRole> projRoles = new ArrayList<ProjRole>();
                try
                {
                    Utils.fillDataLists(data.getRoleGroupFieldData(cf.getId()), groups, projRoles);
                }
                catch (JSONException e)
                {
                    log.error("AdRoleGroupUserCfService::fillLists - Incorrect field data", e);
                    //--> impossible
                }
                fdata.getGroups().addAll(groups);
                fdata.getRoles().addAll(projRoles);

                List<String> highlightedGroups = new ArrayList<String>();
                List<ProjRole> highlightedProjRoles = new ArrayList<ProjRole>();
                try
                {
                    Utils.fillDataLists(data.getHighlightedRoleGroupFieldData(cf.getId()), highlightedGroups, highlightedProjRoles);
                }
                catch (JSONException e)
                {
                    log.error("AdRoleGroupUserCfService::fillLists - Incorrect field data", e);
                    //--> impossible
                }
                fdata.getHighlightedGroups().addAll(highlightedGroups);
                fdata.getHighlightedRoles().addAll(highlightedProjRoles);

                if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:single_role_group_usercf"))
                {
                    singleFields.put(fdata.getFieldId(), fdata);
                }
                else
                {
                    multiFields.put(fdata.getFieldId(), fdata);
                }
            }
            else if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:single_selected_usercf") ||
                     cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:multi_selected_usercf"))
            {
                SelectedFieldData fdata = new SelectedFieldData(cf.getId(), cf.getName());
                fdata.addUsers(data.getStoredUsers(cf.getId()));
                if (cf.isAllProjects())
                {
                    fdata.setAllProjects(true);
                }
                else
                {
                    fdata.setAllProjects(false);
                    List<String> fieldProjs = new ArrayList<String>();
                    List<Project> projs = cf.getAssociatedProjectObjects();
                    for (Project proj : projs)
                    {
                        fieldProjs.add(proj.getName());
                    }

                    fdata.setProjects(fieldProjs);
                }

                if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.userpickers:single_selected_usercf"))
                {
                    singleSelectedFields.put(fdata.getFieldId(), fdata);
                }
                else
                {
                    multiSelectedFields.put(fdata.getFieldId(), fdata);
                }
            }
        }

        return SUCCESS;
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

    public Map<String, SelectedFieldData> getMultiSelectedFields()
    {
        return multiSelectedFields;
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

    public Map<String, SelectedFieldData> getSingleSelectedFields()
    {
        return singleSelectedFields;
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
