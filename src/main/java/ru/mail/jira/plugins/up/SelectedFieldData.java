/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * This structure keeps selected custom field settings.
 * 
 * @author Andrey Markelov
 */
public class SelectedFieldData
{
    /**
     * Field ID.
     */
    private String fieldId;

    /**
     * Field name.
     */
    private String fieldName;

    /**
     * Is project context?
     */
    private boolean isAllProjects;

    /**
     * Is autocomplete?
     */
    private boolean isAutocomplete;

    /**
     * Is restricted?
     */
    private boolean isRestricted;

    /**
     * Projects.
     */
    private List<String> projects;

    /**
     * Groups.
     */
    private Set<String> users;

    /**
     * Constructor.
     */
    public SelectedFieldData(String fieldId, String fieldName)
    {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.users = new LinkedHashSet<String>();
    }

    public void addUser(String user)
    {
        users.add(user);
    }

    public void addUsers(Set<String> users)
    {
        this.users.addAll(users);
    }

    public String getFieldId()
    {
        return fieldId;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public List<String> getProjects()
    {
        return projects;
    }

    public Set<String> getUsers()
    {
        return users;
    }

    public String getUsersStr()
    {
        StringBuilder sb = new StringBuilder();

        for (String user : users)
        {
            sb.append(user).append(",");
        }

        return sb.toString();
    }

    public boolean isAllProjects()
    {
        return isAllProjects;
    }

    public boolean isAutocomplete()
    {
        return isAutocomplete;
    }

    public void setAllProjects(boolean isAllProjects)
    {
        this.isAllProjects = isAllProjects;
    }

    public void setAutocomplete(boolean isAutocomplete)
    {
        this.isAutocomplete = isAutocomplete;
    }

    public void setProjects(List<String> projects)
    {
        this.projects = projects;
    }

    public boolean isRestricted()
    {
        return isRestricted;
    }

    public void setRestricted(boolean isRestricted)
    {
        this.isRestricted = isRestricted;
    }

    @Override
    public String toString()
    {
        return "SelectedFieldData[fieldId=" + fieldId + ", fieldName="
            + fieldName + ", isAllProjects=" + isAllProjects + ", projects="
            + projects + ", users=" + users + "]";
    }
}
