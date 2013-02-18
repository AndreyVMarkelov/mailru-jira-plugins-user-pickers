/*
 * Created by Andrey Markelov 11-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins.up;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ru.mail.jira.plugins.up.common.Utils;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.avatar.Avatar.Size;
import com.atlassian.jira.avatar.AvatarServiceImpl;
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
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.FieldVisibilityManager;


/**
 * Multi selected users field.
 * 
 * @author Andrey Markelov
 */
public class MultiSelectedUsersCf extends MultiUserCFType
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * User util.
     */
    private final UserUtil userUtil;

    private final String baseUrl;

    private Map<String, String> usersAvatars;

    private final AvatarServiceImpl avatarService;

    /**
     * Constructor.
     */
    public MultiSelectedUsersCf(
        CustomFieldValuePersister customFieldValuePersister,
        StringConverter stringConverter,
        GenericConfigManager genericConfigManager,
        ApplicationProperties applicationProperties,
        JiraAuthenticationContext authenticationContext,
        UserPickerSearchService searchService,
        FieldVisibilityManager fieldVisibilityManager, PluginData data,
        UserUtil userUtil, com.atlassian.sal.api.ApplicationProperties appProp)
    {
        super(customFieldValuePersister, stringConverter, genericConfigManager,
            new MultiUserConverterImpl(userUtil), applicationProperties,
            authenticationContext, searchService, fieldVisibilityManager);
        this.data = data;
        this.userUtil = userUtil;
        baseUrl = appProp.getBaseUrl();

        this.avatarService = ComponentManager
            .getComponentInstanceOfType(AvatarServiceImpl.class);
    }

    @Override
    public Map<String, Object> getVelocityParameters(Issue issue,
        CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> params = super.getVelocityParameters(issue, field,
            fieldLayoutItem);

        Map<String, String> map = new HashMap<String, String>();
        Set<String> users = data.getStoredUsers(field.getId());
        for (String user : users)
        {
            User userObj = userUtil.getUserObject(user);
            if (userObj != null)
            {
                map.put(userObj.getName(), userObj.getDisplayName());
            }
        }

        Object issueValObj = issue.getCustomFieldValue(field);
        Set<String> issueVal = Utils.convertList(issueValObj);
        params.put("selectVal", Utils.convertSetToString(issueVal));

        TreeMap<String, String> sorted_map = new TreeMap<String, String>(
            new ValueComparator(map));
        sorted_map.putAll(map);
        params.put("map", sorted_map);
        params.put("issueVal", issueVal);
        params.put("isautocomplete", data.isAutocompleteView(field.getId()));
        params.put("baseUrl", baseUrl);

        usersAvatars = new HashMap<String, String>(users.size());
        for (String userName : users)
        {
            User user = ComponentManager.getInstance().getUserUtil()
                .getUserObject(userName);
            if (user != null)
            {
                usersAvatars.put(user.getName(), getUserAvatarUrl(user));
            }
        }
        params.put("usersAvatars", usersAvatars);

        Utils.addViewAndEditParameters(params, field.getId());

        return params;
    }

    private String getUserAvatarUrl(User user)
    {
        URI uri = avatarService.getAvatarURL(user, user.getName(), Size.SMALL);

        return uri.toString();
    }
}
