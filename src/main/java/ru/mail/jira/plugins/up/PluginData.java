/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.Set;

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
     * Get stored users.
     */
    Set<String> getStoredUsers(String cfId);

    /**
     * Store role group field Data.
     */
    void storeRoleGroupFieldData(String cfId, String data);

    /**
     * Store users.
     */
    void storeUsers(String cfId, Set<String> users);
}
