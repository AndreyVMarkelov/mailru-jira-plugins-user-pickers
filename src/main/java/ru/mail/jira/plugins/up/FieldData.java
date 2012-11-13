/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.ArrayList;
import java.util.List;

/**
 * This structure keeps custom field settings.
 * 
 * @author Andrey Markelov
 */
public class FieldData
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
     * Groups.
     */
    private List<String> groups;

    /**
     * 
     */
    private boolean isAllProjects;

    /**
     * 
     */
    private List<String> projects;

    /**
     * Project roles.
     */
    private List<ProjRole> roles;

    /**
     * Constructor.
     */
    public FieldData(
        String fieldId,
        String fieldName)
    {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.groups = new ArrayList<String>();
        this.roles = new ArrayList<ProjRole>();
    }

    public void addGroup(String group)
    {
        groups.add(group);
    }

    public void addGroups(List<String> groups)
    {
        this.groups.addAll(groups);
    }

    public void addRole(ProjRole role)
    {
        roles.add(role);
    }

    public void addRoles(List<ProjRole> roles)
    {
        this.roles.addAll(roles);
    }

    public String getFieldId()
    {
        return fieldId;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public List<String> getGroups()
    {
        return groups;
    }

    public List<String> getProjects()
    {
        return projects;
    }

    public List<ProjRole> getRoles()
    {
        return roles;
    }

    public boolean isAllProjects()
    {
        return isAllProjects;
    }

    public void setAllProjects(boolean isAllProjects)
    {
        this.isAllProjects = isAllProjects;
    }

    public void setProjects(List<String> projects)
    {
        this.projects = projects;
    }

    @Override
    public String toString()
    {
        return "FieldData[fieldId=" + fieldId + ", fieldName=" + fieldName
            + ", groups=" + groups + ", isAllProjects=" + isAllProjects
            + ", projects=" + projects + ", roles=" + roles + "]";
    }
}
