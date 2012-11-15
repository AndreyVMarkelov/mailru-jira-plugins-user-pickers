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
import org.apache.log4j.Logger;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverterImpl;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONException;
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
     * Logger.
     */
    private final Logger log = Logger.getLogger(MultiRoleGroupUserField.class);

    /**
     * Plug-In data.
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
        GenericConfigManager genericConfigManager,
        UserManager userMgr,
        ApplicationProperties applicationProperties,
        JiraAuthenticationContext authenticationContext,
        UserPickerSearchService searchService,
        FieldVisibilityManager fieldVisibilityManager,
        JiraBaseUrls jiraBaseUrls,
        PluginData data,
        GroupManager grMgr,
        ProjectRoleManager projectRoleManager)
    {
        super(
            customFieldValuePersister,
            genericConfigManager,
            new MultiUserConverterImpl(userMgr),
            applicationProperties,
            authenticationContext,
            searchService,
            fieldVisibilityManager,
            jiraBaseUrls);
        this.data = data;
        this.grMgr = grMgr;
        this.projectRoleManager = projectRoleManager;
    }

    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field,
        FieldLayoutItem fieldLayoutItem)
    {
    	Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);

        Map<String, String> map = new HashMap<String, String>();

        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(data.getRoleGroupFieldData(field.getId()), groups, projRoles);
        }
        catch (JSONException e)
        {
            log.error("AdRoleGroupUserCfService::getVelocityParameters - Incorrect field data", e);
            //--> impossible
        }

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
                map.putAll(Utils.getProjectRoleUsers(projectRoleManager, pr.getRole(), proj));
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
        Set<String> issueVal = Utils.convertList(issueValObj);
        params.put("issueVal", issueVal);

        return params;
    }
}
