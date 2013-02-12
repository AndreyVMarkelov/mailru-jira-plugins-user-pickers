/*
 * Created by Andrey Markelov 11-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins.up;


import com.atlassian.jira.bc.user.search.UserPickerSearchService;
import com.atlassian.jira.issue.customfields.converters.UserConverterImpl;
import com.atlassian.jira.issue.customfields.searchers.UserPickerSearcher;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.resolver.UserResolver;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.FieldVisibilityManager;


/**
 * Single searcher.
 * 
 * @author Andrey Markelov
 */
public class SingleSearcher extends UserPickerSearcher
{
    /**
     * Constructor.
     */
    public SingleSearcher(UserResolver userResolver,
        JqlOperandResolver operandResolver, JiraAuthenticationContext context,
        UserPickerSearchService userPickerSearchService,
        CustomFieldInputHelper customFieldInputHelper,
        UserManager userManager,
        UserManager userMgr,
        FieldVisibilityManager fieldVisibilityManager)
    {
        super(
            userResolver,
            operandResolver,
            context,
            new UserConverterImpl(userMgr),
            userPickerSearchService,
            customFieldInputHelper,
            userManager,
            fieldVisibilityManager);
    }
}
