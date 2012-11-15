/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Implementation of <code>PluginData</code>.
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

    @Override
    public Set<String> getStoredUsers(String cfId)
    {
        Set<String> users = new LinkedHashSet<String>();

        String data = getStringProperty(cfId + ".grscf");
        if (data == null)
        {
            return users;
        }

        StringTokenizer st = new StringTokenizer(data, ",");
        while (st.hasMoreTokens())
        {
            users.add(st.nextToken().trim());
        }

        return users;
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

    @Override
    public void storeUsers(String cfId, Set<String> users)
    {
        if (users == null)
        {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String user : users)
        {
            sb.append(user).append(",");
        }

        setStringProperty(cfId + ".grscf", sb.toString());
    }
}
