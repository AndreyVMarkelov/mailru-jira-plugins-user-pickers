package ru.mail.jira.plugins.up;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ru.mail.jira.plugins.up.common.Utils;

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
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.UserHistoryManager;
import com.atlassian.jira.user.util.UserManager;

/**
 * Single selected users field.
 * 
 * @author Andrey Markelov
 */
public class SelectedUsersCf extends UserCFType
{
    /**
     * Plug-In data.
     */
    private final PluginData data;

    /**
     * User manager.
     */
    private final UserManager userMgr;

    private final String baseUrl;

    public SelectedUsersCf(
            CustomFieldValuePersister customFieldValuePersister,
            GenericConfigManager genericConfigManager,
            ApplicationProperties applicationProperties,
            JiraAuthenticationContext authenticationContext,
            UserPickerSearchService searchService,
            JiraBaseUrls jiraBaseUrls,
            UserHistoryManager userHistoryManager,
            PluginData data,
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
        this.userMgr = userMgr;
        this.baseUrl = appProp.getBaseUrl();
    }

    @Override
    public Map<String, Object> getVelocityParameters(
            Issue issue,
            CustomField field,
            FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);

        Map<String, String> map = new HashMap<String, String>();
        Set<String> users;
        if (data.isRestricted(field.getId()))
        {
            users = data.getStoredUsers(field.getId());
        }
        else
        {
            users = Utils.getAllUsers();
        }
        for (String user : users)
        {
            User userObj = userMgr.getUserObject(user);
            if (userObj != null)
            {
                map.put(userObj.getName(), userObj.getDisplayName());
            }
        }

        TreeMap<String, String> sorted_map = new TreeMap<String, String>(
            new ValueComparator(map));
        sorted_map.putAll(map);
        params.put("map", sorted_map);
        params.put("isautocomplete", data.isAutocompleteView(field.getId()));
        params.put("baseUrl", baseUrl);
        params.put("isrestricted", data.isRestricted(field.getId()));
        Utils.addViewAndEditParameters(params, field.getId());

        Utils.addViewAndEditParameters(params, field.getId());

        return params;
    }
}
