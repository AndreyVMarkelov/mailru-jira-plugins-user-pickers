/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import ru.mail.jira.plugins.up.common.Utils;
import ru.mail.jira.plugins.up.structures.ProjRole;
import org.apache.log4j.Logger;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.UserConverterImpl;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.UserHistoryManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONException;

/**
 * Role group single custom field.
 * 
 * @author Andrey Markelov
 */
public class RoleGroupUserField
    extends UserCFType
{
    /**
     * Logger.
     */
    private final Logger log = Logger.getLogger(RoleGroupUserField.class);

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

    private final String baseUrl;

    public RoleGroupUserField(
            CustomFieldValuePersister customFieldValuePersister,
            GenericConfigManager genericConfigManager,
            ApplicationProperties applicationProperties,
            JiraAuthenticationContext authenticationContext,
            UserPickerSearchService searchService,
            JiraBaseUrls jiraBaseUrls,
            UserHistoryManager userHistoryManager,
            PluginData data,
            GroupManager grMgr,
            ProjectRoleManager projectRoleManager,
            UserManager userMgr,
            com.atlassian.sal.api.ApplicationProperties appProp) {
        super(
            customFieldValuePersister,
            new UserConverterImpl(userMgr),
            genericConfigManager,
            applicationProperties,
            authenticationContext,
            searchService,
            jiraBaseUrls,
            userHistoryManager);
        this.data = data;
        this.grMgr = grMgr;
        this.projectRoleManager = projectRoleManager;
        this.baseUrl = appProp.getBaseUrl();
    }


    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field,
        FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);

        /* Load custom field parameters */
        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(data.getRoleGroupFieldData(field.getId()), groups, projRoles, data.isRestricted(field.getId()));
        }
        catch (JSONException e)
        {
            log.error("RoleGroupUserField::getVelocityParameters - Incorrect field data", e);
            // --> impossible
        }

        List<String> highlightedGroups = new ArrayList<String>();
        List<ProjRole> highlightedProjRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(
                data.getHighlightedRoleGroupFieldData(field.getId()),
                highlightedGroups, highlightedProjRoles, true);
        }
        catch (JSONException e)
        {
            log.error(
                "RoleGroupUserField::getVelocityParameters - Incorrect field data",
                e);
            // --> impossible
        }

        /* Build possible values list */

        Project proj = null;
        if (issue != null) {
            proj = issue.getProjectObject();
        }
        SortedSet<User> possibleUsers = Utils.buildUsersList(
            grMgr,
            projectRoleManager,
            proj,
            groups,
            projRoles);
        Set<User> allUsers = new HashSet<User>(possibleUsers);
        SortedSet<User> highlightedUsers = Utils.buildUsersList(
            grMgr,
            projectRoleManager,
            proj,
            highlightedGroups,
            highlightedProjRoles);
        highlightedUsers.retainAll(possibleUsers);
        possibleUsers.removeAll(highlightedUsers);

        Map<String, String> highlightedUsersSorted = new LinkedHashMap<String, String>();
        Map<String, String> otherUsersSorted = new LinkedHashMap<String, String>();
        for (User user : highlightedUsers)
        {
            highlightedUsersSorted.put(user.getName(), user.getDisplayName());
        }
        for (User user : possibleUsers)
        {
            otherUsersSorted.put(user.getName(), user.getDisplayName());
        }

        params.put("allUsers", allUsers);
        params.put("isautocomplete", data.isAutocompleteView(field.getId()));
        params.put("baseUrl", baseUrl);
        params.put("isrestricted", data.isRestricted(field.getId()));
        params.put("highlightedUsersSorted", highlightedUsersSorted);
        params.put("otherUsersSorted", otherUsersSorted);
        params.put("map", otherUsersSorted);

        Utils.addViewAndEditParameters(params, field.getId());

        Utils.addViewAndEditParameters(params, field.getId());

        return params;
    }
}
