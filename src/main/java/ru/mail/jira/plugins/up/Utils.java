/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());
    }

    /**
     * Private constructor.
     */
    private Utils() {}
}
