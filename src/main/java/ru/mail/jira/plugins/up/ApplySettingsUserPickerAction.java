package ru.mail.jira.plugins.up;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ApplySettingsUserPickerAction extends JiraWebActionSupport {
    private static final long serialVersionUID = -5231857472475942619L;

    public ApplySettingsUserPickerAction() {
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
}
