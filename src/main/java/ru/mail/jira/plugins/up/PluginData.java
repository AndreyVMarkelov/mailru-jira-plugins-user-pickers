/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

/**
 * Plug-In data keeper.
 * 
 * @author Andrey Markelov
 */
public interface PluginData
{
    /**
     * Get role group field stored data.
     */
    String getRoleGroupFieldData(String cfId);

    /**
     * Store role group field Data.
     */
    void storeRoleGroupFieldData(String cfId, String data);
}
