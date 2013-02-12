/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * This structure keeps custom field settings.
 * 
 * @author Andrey Markelov
 */
public class FieldData
{
    private final String fieldId;
    private final String fieldName;
    private final List<String> groups = new ArrayList<String>();
    private final List<ProjRole> roles = new ArrayList<ProjRole>();
    private final List<String> highlightedGroups = new ArrayList<String>();
    private final List<ProjRole> highlightedRoles = new ArrayList<ProjRole>();
    private boolean isAllProjects;
    private List<String> projects;
    private boolean isAutocomplete;

    public FieldData(
        String fieldId,
        String fieldName)
    {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
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

    public List<ProjRole> getRoles()
    {
        return roles;
    }

    public List<String> getHighlightedGroups()
    {
        return highlightedGroups;
    }

    public List<ProjRole> getHighlightedRoles()
    {
        return highlightedRoles;
    }

    public boolean isAllProjects()
    {
        return isAllProjects;
    }

    public void setAllProjects(boolean isAllProjects)
    {
        this.isAllProjects = isAllProjects;
    }

    public List<String> getProjects()
    {
        return projects;
    }

    public void setProjects(List<String> projects)
    {
        this.projects = projects;
    }
    
    public boolean isAutocomplete()
    {
        return isAutocomplete;
    }

    public void setAutocomplete(boolean isAutocomplete)
    {
        this.isAutocomplete = isAutocomplete;
    }

    @Override
    public String toString()
    {
        return "FieldData[fieldId=" + fieldId + ", fieldName=" + fieldName +
                ", groups=" + groups + ", roles=" + roles +
                ", highlightedGroups=" + highlightedGroups + ", highlightedRoles=" + highlightedRoles +
                ", isAllProjects=" + isAllProjects + ", projects=" + projects + "]";
    }
}
