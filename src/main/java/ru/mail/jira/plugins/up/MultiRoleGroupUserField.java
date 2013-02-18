/*
 * Created by Andrey Markelov 11-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins.up;


import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import ru.mail.jira.plugins.up.common.Utils;
import ru.mail.jira.plugins.up.structures.ProjRole;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.avatar.Avatar.Size;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverterImpl;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
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
public class MultiRoleGroupUserField extends MultiUserCFType
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

    private final String baseUrl;

    private Map<String, String> usersAvatars;

    /**
     * Constructor.
     */

    public MultiRoleGroupUserField(
        CustomFieldValuePersister customFieldValuePersister,
        GenericConfigManager genericConfigManager, UserManager userMgr,
        ApplicationProperties applicationProperties,
        JiraAuthenticationContext authenticationContext,
        UserPickerSearchService searchService,
        FieldVisibilityManager fieldVisibilityManager,
        JiraBaseUrls jiraBaseUrls, PluginData data, GroupManager grMgr,
        ProjectRoleManager projectRoleManager,
        com.atlassian.sal.api.ApplicationProperties appProp)
    {
        super(customFieldValuePersister, genericConfigManager,
            new MultiUserConverterImpl(userMgr), applicationProperties,
            authenticationContext, searchService, fieldVisibilityManager,
            jiraBaseUrls);
        this.data = data;
        this.grMgr = grMgr;
        this.projectRoleManager = projectRoleManager;
        baseUrl = appProp.getBaseUrl();
    }

    @Override
    public Map<String, Object> getVelocityParameters(Issue issue,
        CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> params = super.getVelocityParameters(issue, field,
            fieldLayoutItem);

        /* Load custom field parameters */

        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(data.getRoleGroupFieldData(field.getId()),
                groups, projRoles);
        }
        catch (JSONException e)
        {
            log.error(
                "AdRoleGroupUserCfService::getVelocityParameters - Incorrect field data",
                e);
            // --> impossible
        }

        List<String> highlightedGroups = new ArrayList<String>();
        List<ProjRole> highlightedProjRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(
                data.getHighlightedRoleGroupFieldData(field.getId()),
                highlightedGroups, highlightedProjRoles);
        }
        catch (JSONException e)
        {
            log.error(
                "MultiRoleGroupUserField::getVelocityParameters - Incorrect field data",
                e);
            // --> impossible
        }

        /* Build possible values list */

        SortedSet<User> possibleUsers = Utils.buildUsersList(grMgr,
            projectRoleManager, issue.getProjectObject(), groups, projRoles);
        Set<User> allUsers = new HashSet<User>(possibleUsers);
        SortedSet<User> highlightedUsers = Utils.buildUsersList(grMgr,
            projectRoleManager, issue.getProjectObject(), highlightedGroups,
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
        params.put("highlightedUsersSorted", highlightedUsersSorted);
        params.put("otherUsersSorted", otherUsersSorted);

        /* Prepare selected values */
        Object issueValObj = issue.getCustomFieldValue(field);
        Set<String> issueVal = Utils.convertList(issueValObj);

        params.put("selectVal", Utils.convertSetToString(issueVal));
        params.put("issueVal", issueVal);
        params.put("isautocomplete", data.isAutocompleteView(field.getId()));
        params.put("baseUrl", baseUrl);

        usersAvatars = new HashMap<String, String>(allUsers.size());
        for (User user : allUsers)
        {
            usersAvatars.put(user.getName(), getUserAvatarUrl(user));
        }
        params.put("usersAvatars", usersAvatars);

        Utils.addViewAndEditParameters(params, field.getId());

        return params;
    }

    private String getUserAvatarUrl(User user)
    {
        URI uri = ComponentAccessor.getAvatarService().getAvatarAbsoluteURL(
            user, user.getName(), Size.SMALL);

        return uri.toString();
    }
}
