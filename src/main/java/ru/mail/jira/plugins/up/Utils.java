/*
 * Created by Andrey Markelov 11-11-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins.up;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

/**
 * This class contains utility methods.
 * 
 * @author Andrey Markelov
 */
public class Utils
{
    /**
     * Convert string list to java list.
     */
    public static List<String> convertList(Object objStr)
    {
        List<String> list = new ArrayList<String>();
        if (objStr == null)
        {
            return list;
        }

        String str = removeBrackets(objStr.toString());
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken().trim());
        }

        return list;
    }

    /**
     * Get base URL from HTTP request.
     */
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());
    }

    /**
     * Remove brackets from string.
     */
    public static String removeBrackets(String str)
    {
        if (str == null || str.length() == 0)
        {
            return "";
        }

        if (str.startsWith("["))
        {
            str = str.substring(1);
        }

        if (str.endsWith("]"))
        {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    /**
     * Private constructor.
     */
    private Utils() {}
}
