/*
 * Created by Andrey Markelov 11-11-2012. Copyright Mail.Ru Group 2012. All
 * rights reserved.
 */
package ru.mail.jira.plugins.up;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import org.ofbiz.core.entity.GenericValue;

import ru.mail.jira.plugins.up.common.Consts;
import ru.mail.jira.plugins.up.common.Utils;
import ru.mail.jira.plugins.up.structures.HtmlEntity;
import ru.mail.jira.plugins.up.structures.ProjRole;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.security.xsrf.XsrfTokenGenerator;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.json.JSONException;


/**
 * Plug-In service.
 * 
 * @author Andrey Markelov
 */
@Path("/usrpickerssrv")
public class AdRoleGroupUserCfService
{
    /**
     * Plugin data.
     */
    private final PluginData data;

    /**
     * Group manager.
     */
    private final GroupManager groupMgr;

    /**
     * Logger.
     */
    private final Logger log = Logger.getLogger(AdRoleGroupUserCfService.class);

    /**
     * Project manager.
     */
    private final ProjectManager prMgr;

    /**
     * Project role manager.
     */
    private final ProjectRoleManager projectRoleManager;

    /**
     * Constructor.
     */
    public AdRoleGroupUserCfService(PluginData data, GroupManager groupMgr,
        ProjectManager prMgr, ProjectRoleManager projectRoleManager)
    {
        this.data = data;
        this.groupMgr = groupMgr;
        this.prMgr = prMgr;
        this.projectRoleManager = projectRoleManager;
    }

    @POST
    @Path("/confselectedsettingsdlg")
    @Produces({MediaType.APPLICATION_JSON})
    public Response configureSelectedSettingsDialog(
        @Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("AdRoleGroupUserCfService::configureSelectedSettingsDialog - User is not logged");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.notlogged"))
                .status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager
            .getComponentInstanceOfType(XsrfTokenGenerator.class);
        String token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(token))
        {
            log.error("AdRoleGroupUserCfService::configureSelectedSettingsDialog - There is no token");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                .status(500).build();
        }
        else
        {
            String atl_token = req.getParameter("atl_token");
            if (!atl_token.equals(token))
            {
                log.error("AdRoleGroupUserCfService::configureSelectedSettingsDialog - Token is invalid");
                return Response
                    .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                    .status(500).build();
            }
        }

        String[] selUsers = req.getParameterValues("selUsers");
        String cfIdStr = req.getParameter("cfId");
        String autocompleteFlag = req.getParameter("autocomplete-flag");

        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            return Response.status(500).build();
        }

        Set<String> setUsers = new LinkedHashSet<String>();
        if (selUsers != null)
        {
            for (String usr : selUsers)
            {
                if (usr.length() != 0)
                {
                    setUsers.add(usr);
                }
            }
        }

        data.storeUsers(cfIdStr, setUsers);
        data.setAutocompleteView(cfIdStr,
            Consts.CHECKBOX_CHECKED.equals(autocompleteFlag));

        return Response.ok().build();
    }

    @POST
    @Path("/configuresingle")
    @Produces({MediaType.APPLICATION_JSON})
    public Response configureSingleField(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("AdRoleGroupUserCfService::configureSingleField - User is not logged");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.notlogged"))
                .status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager
            .getComponentInstanceOfType(XsrfTokenGenerator.class);
        String token = xsrfTokenGenerator.getToken(req);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(token))
        {
            log.error("AdRoleGroupUserCfService::configureSingleField - There is no token");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                .status(500).build();
        }
        else
        {
            String atl_token = req.getParameter("atl_token");
            if (!atl_token.equals(token))
            {
                log.error("AdRoleGroupUserCfService::configureSingleField - Token is invalid");
                return Response
                    .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                    .status(500).build();
            }
        }

        String cfIdStr = req.getParameter("cfId");
        String shares_data = req.getParameter("shares_data");
        String autocompleteFlag = req.getParameter("autocomplete-flag");

        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            log.error("AdRoleGroupUserCfService::configureSingleField - Incorrect parameters");
            return Response.status(500).build();
        }

        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(shares_data, groups, projRoles);
        }
        catch (JSONException e)
        {
            log.error(
                "AdRoleGroupUserCfService::configureSingleField - Incorrect parameters",
                e);
            return Response.status(500).build();
        }

        String highlighted_data = req.getParameter("highlighted_data");
        List<String> highlightedGroups = new ArrayList<String>();
        List<ProjRole> highlightedProjRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(highlighted_data, highlightedGroups, highlightedProjRoles);
        }
        catch (JSONException e)
        {
            log.error("AdRoleGroupUserCfService::configureSingleField - Incorrect parameters", e);
            return Response.status(500).build();
        }

        data.storeRoleGroupFieldData(cfIdStr, shares_data);
        data.setAutocompleteView(cfIdStr,
            Consts.CHECKBOX_CHECKED.equals(autocompleteFlag));
        data.storeHighlightedRoleGroupFieldData(cfIdStr, highlighted_data);

        return Response.ok().build();
    }

    @POST
    @Path("/initselectedsettingsdlg")
    @Produces({MediaType.APPLICATION_JSON})
    public Response initSelectedSettingsDialog(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("AdRoleGroupUserCfService::initSelectedSettingsDialog - User is not logged");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.notlogged"))
                .status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager
            .getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.generateToken(req);

        String cfIdStr = req.getParameter("cfId");
        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            log.error("AdRoleGroupUserCfService::initSelectedSettingsDialog - Incorrect parameters");
            return Response.status(500).build();
        }

        Map<String, String> userMap = new LinkedHashMap<String, String>();
        Collection<User> users = ComponentManager.getInstance().getUserUtil()
            .getUsers();
        for (User userObj : users)
        {
            userMap.put(userObj.getName(), userObj.getDisplayName());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("i18n", authCtx.getI18nHelper());
        params.put("baseUrl", Utils.getBaseUrl(req));
        params.put("atl_token", atl_token);
        params.put("cfId", cfIdStr);
        params.put("userMap", userMap);
        params.put("selData", data.getStoredUsers(cfIdStr));
        params.put("isautocomplete", data.isAutocompleteView(cfIdStr));

        try
        {
            String body = ComponentAccessor.getVelocityManager().getBody(
                "templates/aduserpicker/", "selectedsettingsdlg.vm", params);
            return Response.ok(new HtmlEntity(body)).build();
        }
        catch (VelocityException vex)
        {
            log.error(
                "AdRoleGroupUserCfService::initSelectedSettingsDialog - Velocity parsing error",
                vex);
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                .status(500).build();
        }
    }

    @POST
    @Path("/initsettingsdlg")
    @Produces({MediaType.APPLICATION_JSON})
    public Response initSettingsDialog(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("AdRoleGroupUserCfService::initSettingsDialog - User is not logged");
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.notlogged"))
                .status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager
            .getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.generateToken(req);

        String cfIdStr = req.getParameter("cfId");
        if (cfIdStr == null || cfIdStr.length() == 0)
        {
            log.error("AdRoleGroupUserCfService::initSettingsDialog - Incorrect parameters");
            return Response.status(500).build();
        }

        Map<String, String> projs = new TreeMap<String, String>();
        CustomField cf = ComponentManager.getInstance().getCustomFieldManager()
            .getCustomFieldObject(cfIdStr);
        if (!cf.isAllProjects())
        {
            List<GenericValue> aProjs = cf.getAssociatedProjects();
            for (GenericValue proj : aProjs)
            {
                projs.put(proj.get("id").toString(), (String) proj.get("name"));
            }
        }
        else
        {
            List<Project> projects = prMgr.getProjectObjects();
            if (projects != null)
            {
                for (Project project : projects)
                {
                    projs.put(project.getId().toString(), project.getName());
                }
            }
        }

        Map<String, String> roleProjs = new TreeMap<String, String>();
        Collection<ProjectRole> roles = projectRoleManager.getProjectRoles();
        if (roles != null)
        {
            for (ProjectRole role : roles)
            {
                roleProjs.put(role.getId().toString(), role.getName());
            }
        }

        String sharedData = data.getRoleGroupFieldData(cfIdStr);
        if (sharedData == null || sharedData.length() == 0)
        {
            sharedData = "[]";
        }
        List<String> groups = new ArrayList<String>();
        List<ProjRole> projRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(sharedData, groups, projRoles);
        }
        catch (JSONException e)
        {
            log.error("AdRoleGroupUserCfService::configureSingleField - Incorrect parameters", e);
            return Response.status(500).build();
        }

        String highlightedData = data.getHighlightedRoleGroupFieldData(cfIdStr);
        if (highlightedData == null || highlightedData.length() == 0)
        {
            highlightedData = "[]";
        }
        List<String> highlightedGroups = new ArrayList<String>();
        List<ProjRole> highlightedProjRoles = new ArrayList<ProjRole>();
        try
        {
            Utils.fillDataLists(highlightedData, highlightedGroups, highlightedProjRoles);
        }
        catch (JSONException e)
        {
            log.error(
                "AdRoleGroupUserCfService::configureSingleField - Incorrect parameters",
                e);
            return Response.status(500).build();
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("i18n", authCtx.getI18nHelper());
        params.put("baseUrl", Utils.getBaseUrl(req));
        params.put("atl_token", atl_token);
        params.put("cfId", cfIdStr);
        params.put("allGroups", groupMgr.getAllGroups());
        params.put("projs", projs);
        params.put("roleProjs", roleProjs);
        params.put("storedShares", sharedData);
        params.put("groups", groups);
        params.put("projRoles", projRoles);
        params.put("isautocomplete", data.isAutocompleteView(cfIdStr));
        params.put("highlightedData", highlightedData);
        params.put("highlightedGroups", highlightedGroups);
        params.put("highlightedProjRoles", highlightedProjRoles);

        try
        {
            String body = ComponentAccessor.getVelocityManager().getBody(
                "templates/aduserpicker/", "settingsdlg.vm", params);
            return Response.ok(new HtmlEntity(body)).build();
        }
        catch (VelocityException vex)
        {
            log.error(
                "AdRoleGroupUserCfService::initSettingsDialog - Velocity parsing error",
                vex);
            return Response
                .ok(i18n.getText("jrole-group-usercf.error.internalerror"))
                .status(500).build();
        }
    }
}
