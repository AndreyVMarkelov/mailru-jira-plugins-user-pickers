// Created by Andrey Markelov 11-11-2012.
// Copyright Mail.Ru Group 2012. All rights reserved.

//--> init settings dialog
function initSettingsDlg(baseUrl, cfId)
{
    var res = "";
    jQuery.ajax({
        url: baseUrl + "/rest/rolegroupusercfws/1.0/usrpickerssrv/initsettingsdlg",
        type: "POST",
        dataType: "json",
        data: {"cfId": cfId},
        async: false,
        error: function(xhr, ajaxOptions, thrownError) {
            try {
                var respObj = eval("(" + xhr.responseText + ")");
                initErrorDlg(respObj.message).show();
            } catch(e) {
                initErrorDlg(xhr.responseText).show();
            }
        },
        success: function(result) {
            res = result.html;
        }
    });

    return res;
}

//--> init selected settings dialog
function initSelectedSettingsDlg(baseUrl, cfId)
{
    var res = "";
    jQuery.ajax({
        url: baseUrl + "/rest/rolegroupusercfws/1.0/usrpickerssrv/initselectedsettingsdlg",
        type: "POST",
        dataType: "json",
        data: {"cfId": cfId},
        async: false,
        error: function(xhr, ajaxOptions, thrownError) {
            try {
                var respObj = eval("(" + xhr.responseText + ")");
                initErrorDlg(respObj.message).show();
            } catch(e) {
                initErrorDlg(xhr.responseText).show();
            }
        },
        success: function(result) {
            res = result.html;
        }
    });

    return res;
}

//--> initialize error dialog
function initErrorDlg(bodyText) {
    var errorDialog = new AJS.Dialog({
        width:420,
        height:250,
        id:"error-dialog",
        closeOnOutsideClick: true
    });

    errorDialog.addHeader(AJS.I18n.getText("queryfields.admin.title.error"));
    errorDialog.addPanel("ErrorMainPanel", '' +
        '<html><body><div class="error-message errdlg">' +
        bodyText +
        '</div></body></html>',
        "error-panel-body");
    errorDialog.addCancel(AJS.I18n.getText("queryfields.closebtn"), function() {
        errorDialog.hide();
    });

    return errorDialog;
}

//--> configure single role/group field
function configureSingleField(event, baseUrl, cfId) {
    event.preventDefault();

    var dialogBody = initSettingsDlg(baseUrl, cfId);
    if (!dialogBody)
    {
        return;
    }

    jQuery("#configure_jql_dialog").remove();
    var md = new AJS.Dialog({
        width:650,
        height:350,
        id:"configure_jql_dialog",
        closeOnOutsideClick: true
    });
    md.addHeader(AJS.I18n.getText("jrole-group-usercf.configjqltitle"));
    md.addPanel("load_panel", dialogBody);
    md.addButton(AJS.I18n.getText("jrole-group-usercf.applyjqlbtn"), function() {
        jQuery.ajax({
            url: baseUrl + "/rest/rolegroupusercfws/1.0/usrpickerssrv/configuresingle",
            type: "POST",
            dataType: "json",
            data: AJS.$("#singlesettingsform").serialize(),
            async: false,
            error: function(xhr, ajaxOptions, thrownError) {
                var errText;
                try {
                    var respObj = eval("(" + xhr.responseText + ")");
                    if (respObj.message) {
                        errText = respObj.message;
                    } else if (respObj.html) {
                        errText = respObj.html;
                    } else {
                        errText = xhr.responseText;
                    }
                } catch(e) {
                    errText = xhr.responseText;
                }
                jQuery("#errorpart").empty();
                jQuery("#errorpart").append("<div class='errdiv'>" + errText + "</div>");
            },
            success: function(result) {
                document.location.reload(true);
            }
        });
    });
    md.addCancel(AJS.I18n.getText("jrole-group-usercf.closebtn"), function() {
        md.hide();
    });
    md.show();
}

//--> configure single selected field
function configureSelectedSingleField(event, baseUrl, cfId) {
    event.preventDefault();

    var dialogBody = initSelectedSettingsDlg(baseUrl, cfId);
    if (!dialogBody)
    {
        return;
    }

    jQuery("#configure_jql_dialog").remove();
    var md = new AJS.Dialog({
        width:550,
        height:350,
        id:"configure_jql_dialog",
        closeOnOutsideClick: true
    });
    md.addHeader(AJS.I18n.getText("jrole-group-usercf.configjqltitle"));
    md.addPanel("load_panel", dialogBody);
    md.addButton(AJS.I18n.getText("jrole-group-usercf.applyjqlbtn"), function() {
        jQuery.ajax({
            url: baseUrl + "/rest/rolegroupusercfws/1.0/usrpickerssrv/confselectedsettingsdlg",
            type: "POST",
            dataType: "json",
            data: AJS.$("#singlesettingsselform").serialize(),
            async: false,
            error: function(xhr, ajaxOptions, thrownError) {
                var errText;
                try {
                    var respObj = eval("(" + xhr.responseText + ")");
                    if (respObj.message) {
                        errText = respObj.message;
                    } else if (respObj.html) {
                        errText = respObj.html;
                    } else {
                        errText = xhr.responseText;
                    }
                } catch(e) {
                    errText = xhr.responseText;
                }
                jQuery("#errorpart").empty();
                jQuery("#errorpart").append("<div class='errdiv'>" + errText + "</div>");
            },
            success: function(result) {
                document.location.reload(true);
            }
        });
    });
    md.addCancel(AJS.I18n.getText("jrole-group-usercf.closebtn"), function() {
        md.hide();
    });
    md.show();
}

function addGroup(dataInputId, listDivId, groupsSelectId) {
    var sharesObj = jQuery.evalJSON(jQuery("#" + dataInputId).val());

    var group = jQuery("#" + groupsSelectId + " :selected");
    var grId = "group" + jQuery(group).val();

    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == grId) {
            jQuery("#" + listDivId + " #" + grId).animate({backgroundColor: "red"}, 500, function() { jQuery(this).animate({backgroundColor: "white"}, 500);});
            return;
        }
    }

    var itemObj = new Object();
    itemObj["id"] = grId;
    itemObj["type"] = "G";
    itemObj["group"] = jQuery(group).val();
    sharesObj.push(itemObj);
    jQuery("#" + dataInputId).val(jQuery.toJSON(sharesObj));

    var newElem = jQuery("<div id='" + grId + "'><span>" + AJS.format(AJS.I18n.getText("jrole-group-usercf.share_group"), jQuery(group).text()) + "</span></div>");
    jQuery("#" + listDivId).append(newElem);
    jQuery("#" + listDivId + " #share_trash_sh").clone().attr('id', '').show().appendTo(newElem);
}

function addProject(dataInputId, listDivId, projectsSelectId, rolesSelectId) {
    var sharesObj = jQuery.evalJSON(jQuery("#" + dataInputId).val());

    var proj = jQuery("#" + projectsSelectId + " :selected");
    var role = jQuery("#" + rolesSelectId + " :selected");
    var prId = "project" + jQuery(proj).val() + "role" + jQuery(role).val();

    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == prId) {
            jQuery("#" + listDivId + " #" + prId).animate({backgroundColor: "red"}, 500, function() { jQuery(this).animate({backgroundColor: "white"}, 500);});
            return;
        }
    }

    var itemObj = new Object();
    itemObj["id"] = prId;
    itemObj["type"] = "P";
    itemObj["proj"] = jQuery(proj).val();
    itemObj["role"] = jQuery(role).val();
    sharesObj.push(itemObj);
    jQuery("#" + dataInputId).val(jQuery.toJSON(sharesObj));

    var textVal;
    if (jQuery(role).val()) {
        textVal = AJS.format(AJS.I18n.getText("jrole-group-usercf.share_project_role"), jQuery(proj).text(), jQuery(role).text());
    } else {
        textVal = AJS.format(AJS.I18n.getText("jrole-group-usercf.share_project"), jQuery(proj).text());
    }

    var newElem = jQuery("<div id='" + prId + "'><span>" + textVal + "</span></div>");
    jQuery("#" + listDivId).append(newElem);
    jQuery("#" + listDivId + " #share_trash_sh").clone().attr('id', '').show().appendTo(newElem);
}

function removeGroup(event, dataInputId) {
    var source = event.target || event.srcElement;
    var parent = jQuery(source).parent();
    var parentId = jQuery(parent).attr("id");

    var sharesObj = jQuery.evalJSON(jQuery("#" + dataInputId).val());
    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == parentId) {
            sharesObj.splice(objId, 1);
        }
    }
    jQuery("#" + dataInputId).val(jQuery.toJSON(sharesObj));
    jQuery(parent).remove();
}

AJS.$(document).ready(function() {
    jQuery(window).bind('beforeunload', function() {
        
    });
});

//--> share selectors
function setShareGroup() {
    jQuery("#share_group").show();
    jQuery("#share_project").hide();
}

function setShareProject() {
    jQuery("#share_group").hide();
    jQuery("#share_project").show();
}

function adHighlightedGroupsSwitchClick() {
    jQuery("#ad_highlighted_groups").show();
    jQuery("#ad_highlighted_roles").hide();
}

function adHighlightedRolesSwitchClick() {
    jQuery("#ad_highlighted_groups").hide();
    jQuery("#ad_highlighted_roles").show();
}
//<--

