/*
 * Created by Dmitry Miroshnichenko 12-02-2013.
 * Copyright Mail.Ru Group 2013. All rights reserved.
 */
package ru.mail.jira.plugins.up.structures;

public class SingleValueData
{
    private String value;

    private String comment;

    /**
     * Constructor.
     */
    public SingleValueData(
        String value,
        String comment)
    {
        this.value = value;
        this.comment = comment;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public String toString()
    {
        return "SingleValueData[value=" + value + ", comment=" + comment + "]";
    }
}
