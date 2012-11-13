/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.Map;
import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;

public class RoleGroupUserField
    extends UserCFType
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Constructor.
     */
    public RoleGroupUserField(
        CustomFieldValuePersister customFieldValuePersister,
        UserConverter userConverter,
        GenericConfigManager genericConfigManager,
        ApplicationProperties applicationProperties,
        JiraAuthenticationContext authenticationContext,
        UserPickerSearchService searchService,
        PluginData data)
    {
        super(customFieldValuePersister, userConverter, genericConfigManager,
            applicationProperties, authenticationContext, searchService);
        this.data = data;
    }

    @Override
    public Map<String, Object> getVelocityParameters(
        Issue issue,
        CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        return super.getVelocityParameters(issue, field, fieldLayoutItem);
    }

    @Override
    public void validateFromParams(
        CustomFieldParams relevantParams,
        ErrorCollection errorCollectionToAddTo,
        FieldConfig config)
    {
        super.validateFromParams(relevantParams, errorCollectionToAddTo, config);
    }
}
