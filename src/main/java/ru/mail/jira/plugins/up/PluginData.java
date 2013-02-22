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
     * Get highlighted role group field stored data.
     */
    String getHighlightedRoleGroupFieldData(String cfId);

    /**
     * Get role group field stored data.
     */
    String getRoleGroupFieldData(String cfId);

    /**
     * Get stored users.
     */
    Set<String> getStoredUsers(String cfId);

    /**
     * Is autocomplete view?
     */
    boolean isAutocompleteView(String cfId);

    /**
     * Set autocomplete view.
     */
    void setAutocompleteView(String cfId, boolean flag);

    /**
     * Is restricted
     */
    boolean isRestricted(String cfId);
    
    /**
     * Set restricted.
     */
    void setRestricted(String cfId, boolean flag);

    /**
     * Store highlighted role group field Data.
     */
    void storeHighlightedRoleGroupFieldData(String cfId, String data);

    /**
     * Store role group field Data.
     */
    void storeRoleGroupFieldData(String cfId, String data);

    /**
     * Store users.
     */
    void storeUsers(String cfId, Set<String> users);
}
