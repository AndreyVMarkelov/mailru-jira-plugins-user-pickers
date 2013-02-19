/*
 * Created by Dmitry Miroshnichenko 12-02-2013.
 * Copyright Mail.Ru Group 2013. All rights reserved.
 */
package ru.mail.jira.plugins.up.structures;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

@Immutable
@XmlRootElement
public class AutocompleteUniversalData
    extends AbstractSQLDataBean
{
    @XmlElement
    private String description;

    @XmlElement
    private String name;

    @XmlElement
    private String preference;

    @XmlElement
    private String preferenceimage;

    @XmlElement
    private String state;

    @XmlElement
    private String stateimage;

    @XmlElement
    private String type;

    @XmlElement
    private String typeimage;

    /**
     * Default constructor.
     */
    public AutocompleteUniversalData() {}

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    public String getPreference()
    {
        return preference;
    }

    public String getPreferenceimage()
    {
        return preferenceimage;
    }

    public String getState()
    {
        return state;
    }

    public String getStateimage()
    {
        return stateimage;
    }

    public String getType()
    {
        return type;
    }

    public String getTypeimage()
    {
        return typeimage;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPreference(String preference)
    {
        this.preference = preference;
    }

    public void setPreferenceimage(String preferenceimage)
    {
        this.preferenceimage = preferenceimage;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setStateimage(String stateimage)
    {
        this.stateimage = stateimage;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setTypeimage(String typeimage)
    {
        this.typeimage = typeimage;
    }

    @Override
    public String toString()
    {
        return "AutocompleteUniversalData[name=" + name + ", type=" + type
            + ", typeimage=" + typeimage + ", description=" + description
            + ", state=" + state + ", stateimage=" + stateimage
            + ", preference=" + preference + ", preferenceimage=" + preferenceimage + "]";
    }
}
