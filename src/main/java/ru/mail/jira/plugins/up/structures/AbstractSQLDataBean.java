package ru.mail.jira.plugins.up.structures;

public abstract class AbstractSQLDataBean
    implements ISQLDataBean
{
    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getPreference()
    {
        return null;
    }

    @Override
    public String getPreferenceimage()
    {
        return null;
    }

    @Override
    public String getState()
    {
        return null;
    }

    @Override
    public String getStateimage()
    {
        return null;
    }

    @Override
    public String getType()
    {
        return null;
    }

    @Override
    public String getTypeimage()
    {
        return null;
    }

    @Override
    public void setDescription(String description) {}

    @Override
    public void setName(String name) {}

    @Override
    public void setPreference(String preference) {}

    @Override
    public void setPreferenceimage(String preferenceimage) {}

    @Override
    public void setState(String state) {}

    @Override
    public void setStateimage(String stateimage) {}

    @Override
    public void setType(String type) {}

    @Override
    public void setTypeimage(String typeimage) {}
}
