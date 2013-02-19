/*
 * Created by Dmitry Miroshnichenko 12-02-2013.
 * Copyright Mail.Ru Group 2013. All rights reserved.
 */
package ru.mail.jira.plugins.up.structures;

public interface ISQLDataBean
{
    String PROPERTY_NAME_DESCRIPTION = "DESCRIPTION";

    String PROPERTY_NAME_NAME = "NAME";

    String PROPERTY_NAME_PREFERENCE = "PREFERENCE";

    String PROPERTY_NAME_PREFERENCE_IMAGE = "PREFERENCEIMAGE";

    String PROPERTY_NAME_STATE = "STATE";

    String PROPERTY_NAME_STATEIMAGE = "STATEIMAGE";

    String PROPERTY_NAME_TYPE = "TYPE";

    String PROPERTY_NAME_TYPEIMAGE = "TYPEIMAGE";

    public String getDescription();

    public String getName();

    public String getPreference();

    public String getPreferenceimage();

    public String getState();

    public String getStateimage();

    public String getType();

    public String getTypeimage();

    public void setDescription(String description);

    public void setName(String name);

    public void setPreference(String preference);

    public void setPreferenceimage(String preferenceimage);

    public void setState(String state);

    public void setStateimage(String stateimage);

    public void setType(String type);

    public void setTypeimage(String typeimage);
}
