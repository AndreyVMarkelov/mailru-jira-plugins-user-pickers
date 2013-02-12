/*
 * Created by Dmitry Miroshnichenko 12-02-2013. Copyright Mail.Ru Group 2013.
 * All rights reserved.
 */
package ru.mail.jira.plugins.up.structures;


public interface ISQLDataBean
{
    public static final String PROPERTY_NAME_NAME = "NAME";
    public static final String PROPERTY_NAME_TYPE = "TYPE";
    public static final String PROPERTY_NAME_TYPEIMAGE = "TYPEIMAGE";
    public static final String PROPERTY_NAME_DESCRIPTION = "DESCRIPTION";
    public static final String PROPERTY_NAME_STATE = "STATE";
    public static final String PROPERTY_NAME_STATEIMAGE = "STATEIMAGE";
    public static final String PROPERTY_NAME_PREFERENCE = "PREFERENCE";
    public static final String PROPERTY_NAME_PREFERENCE_IMAGE = "PREFERENCEIMAGE";

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getState();

    public void setState(String state);

    public String getStateimage();

    public void setStateimage(String stateimage);

    public String getPreference();

    public void setPreference(String preference);

    public String getPreferenceimage();

    public void setPreferenceimage(String preferenceimage);

    public String getType();

    public void setType(String type);

    public String getTypeimage();

    public void setTypeimage(String typeimage);
}