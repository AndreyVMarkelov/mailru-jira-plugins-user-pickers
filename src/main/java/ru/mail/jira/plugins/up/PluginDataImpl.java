/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * 
 * 
 * @author Andrey Markelov
 */
public class PluginDataImpl
    implements PluginData
{
    /**
     * PlugIn key.
     */
    private static final String PLUGIN_KEY = "MAIL_RU_USER_PICKER";

    /**
     * Plug-In settings factory.
     */
    private final PluginSettingsFactory pluginSettingsFactory;

    /**
     * Constructor.
     */
    public PluginDataImpl(PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String getRoleGroupFieldData(String cfId)
    {
        return getStringProperty(cfId + ".grcf");
    }

    private String getStringProperty(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).get(key);
    }

    private void setStringProperty(String key, String value)
    {
        pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).put(key, value);
    }

    @Override
    public void storeRoleGroupFieldData(String cfId, String data)
    {
        setStringProperty(cfId + ".grcf", data);
    }
}
