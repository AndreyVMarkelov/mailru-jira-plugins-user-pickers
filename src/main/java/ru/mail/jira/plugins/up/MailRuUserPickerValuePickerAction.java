/*
 * Created by Dmitry Miroshnichenko 12-02-2013. Copyright Mail.Ru Group 2013.
 * All rights reserved.
 */
package ru.mail.jira.plugins.up;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ru.mail.jira.plugins.up.common.Utils;
import ru.mail.jira.plugins.up.structures.ProjRole;
import ru.mail.jira.plugins.up.structures.SingleValueData;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;


public class MailRuUserPickerValuePickerAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = 1305062002511270932L;

    private final Logger log = Logger
        .getLogger(MailRuUserPickerValuePickerAction.class);

    private final ApplicationProperties applicationProperties;

    private PluginData settings;

    private Map<String, SingleValueData> cfValues;

    private String cfid;

    private String inputid;

    private String returnid;

    private final GroupManager grMgr;

    private final ProjectRoleManager projectRoleManager;

    public MailRuUserPickerValuePickerAction(CustomFieldManager cfMgr,
        ApplicationProperties applicationProperties, PluginData settings,
        GroupManager grMgr, ProjectRoleManager projectRoleManager)
    {
        this.applicationProperties = applicationProperties;
        this.settings = settings;
        this.grMgr = grMgr;
        this.projectRoleManager = projectRoleManager;
    }

    @Override
    protected String doExecute() throws Exception
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        UserProjectHistoryManager userProjectHistoryManager = ComponentManager
            .getComponentInstanceOfType(UserProjectHistoryManager.class);
        Project currentProject = userProjectHistoryManager.getCurrentProject(
            Permissions.BROWSE, authCtx.getLoggedInUser());

        CustomField cf = ComponentManager.getInstance().getCustomFieldManager()
            .getCustomFieldObject(getCfid());
        if (cf == null)
        {
            log.error("MailRuUserPickerValuePickerAction::doExecute - Invalid cf id");
        }

        UserUtil userUtil = ComponentManager.getInstance().getUserUtil();
        Map<String, SingleValueData> map;
        if (Utils.isOfGroupRoleUserPickerType(cf.getCustomFieldType().getKey()))
        {
            map = new LinkedHashMap<String, SingleValueData>();
            List<String> groups = new ArrayList<String>();
            List<ProjRole> projRoles = new ArrayList<ProjRole>();
            try
            {
                Utils.fillDataLists(settings.getRoleGroupFieldData(getCfid()),
                    groups, projRoles);
            }
            catch (JSONException e)
            {
                log.error(
                    "MailRuUserPickerValuePickerAction::doExecute - Incorrect field data",
                    e);
                // --> impossible
            }

            for (String group : groups)
            {
                Collection<User> users = grMgr.getUsersInGroup(group);
                if (users != null)
                {
                    for (User user : users)
                    {
                        map.put(
                            user.getName(),
                            new SingleValueData(user.getName(), user
                                .getDisplayName()));
                    }
                }
            }

            for (ProjRole pr : projRoles)
            {
                if (currentProject != null
                    && currentProject.getId().toString()
                        .equals(pr.getProject()))
                {
                    Set<String> rolesUsers = Utils.getProjectRoleUsers(
                        projectRoleManager, pr.getRole(), currentProject)
                        .keySet();
                    for (String roleUser : rolesUsers)
                    {
                        User user = userUtil.getUserObject(roleUser);
                        if (user != null)
                        {
                            map.put(
                                user.getName(),
                                new SingleValueData(user.getName(), user
                                    .getDisplayName()));
                        }
                    }
                }
            }
        }
        else
        {
            map = new LinkedHashMap<String, SingleValueData>();
            Set<String> simpleUsers = settings.getStoredUsers(getCfid());
            for (String simpleUser : simpleUsers)
            {
                User user = userUtil.getUserObject(simpleUser);
                if (user != null)
                {
                    map.put(user.getName(), new SingleValueData(user.getName(),
                        user.getDisplayName()));
                }
            }
        }
        if (map != null)
        {
            cfValues = map;
        }

        return super.doExecute();
    }

    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public String getCfid()
    {
        return cfid;
    }

    public void setCfid(String cfid)
    {
        this.cfid = cfid;
    }

    public String getInputid()
    {
        return inputid;
    }

    public void setInputid(String inputid)
    {
        this.inputid = inputid;
    }

    public String getReturnid()
    {
        return returnid;
    }

    public void setReturnid(String returnid)
    {
        this.returnid = returnid;
    }

    public Map<String, SingleValueData> getCfValues()
    {
        return cfValues;
    }
}