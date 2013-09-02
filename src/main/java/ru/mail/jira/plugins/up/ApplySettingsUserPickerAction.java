package ru.mail.jira.plugins.up;

import java.util.Collection;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ApplySettingsUserPickerAction extends JiraWebActionSupport {
    private static final long serialVersionUID = -5231857472475942619L;

    private static ProjectRoleManager roleManager;

    private final ProjectManager prMgr;

    public ApplySettingsUserPickerAction(
            ProjectRoleManager roleManager,
            ProjectManager prMgr) {
        this.roleManager = roleManager;
        this.prMgr = prMgr;
    }

    @Override
    public String doDefault() throws Exception {
        

        return SUCCESS;
    }

    @Override
    protected String doExecute() throws Exception {
        return super.doExecute();
    }

    @Override
    protected void doValidation() {
        super.doValidation();
    }

    public Collection<Group> getGroups() {
        return ComponentAccessor.getGroupManager().getAllGroups();
    }

    public Collection<Project> getProjects() {
        return prMgr.getProjectObjects();
    }

    public Collection<ProjectRole> getRoles() {
        return roleManager.getProjectRoles();
    }

    public Collection<User> getUsers() {
        return ComponentManager.getInstance().getUserUtil().getUsers();
    }
}
