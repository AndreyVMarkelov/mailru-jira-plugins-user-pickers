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
        width:550,
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

function addGroup() {
    var sharesObj = jQuery.evalJSON(jQuery("#shares_data").val());

    var group = jQuery("#groupShare :selected");
    var grId = "group" + jQuery(group).val();

    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == grId) {
            jQuery("#" + grId).animate({backgroundColor: "red"}, 500, function() { jQuery("#" + grId).animate({backgroundColor: "white"}, 500);});
            return;
        }
    }

    var itemObj = new Object();
    itemObj["id"] = grId;
    itemObj["type"] = "G";
    itemObj["group"] = jQuery(group).val();
    sharesObj.push(itemObj);
    jQuery("#shares_data").val(jQuery.toJSON(sharesObj));

    var newElem = "<div id='" + grId + "'><span>" + AJS.format(AJS.I18n.getText("jrole-group-usercf.share_group"), jQuery(group).text()) + "</span></div>";
    jQuery("#share_display_div").append(newElem);
    jQuery("#share_trash_sh").clone().show().appendTo("#" + grId);
}

function addProject() {
    var sharesObj = jQuery.evalJSON(jQuery("#shares_data").val());

    var proj = jQuery("#projectShare-project :selected");
    var role = jQuery("#projectShare-role :selected");
    var prId = "project" + jQuery(proj).val() + "role" + jQuery(role).val();

    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == prId) {
            jQuery("#" + prId).animate({backgroundColor: "red"}, 500, function() { jQuery("#" + prId).animate({backgroundColor: "white"}, 500);});
            return;
        }
    }

    var itemObj = new Object();
    itemObj["id"] = prId;
    itemObj["type"] = "P";
    itemObj["proj"] = jQuery(proj).val();
    itemObj["role"] = jQuery(role).val();
    sharesObj.push(itemObj);
    jQuery("#shares_data").val(jQuery.toJSON(sharesObj));

    var textVal;
    if (jQuery(role).val()) {
        textVal = AJS.format(AJS.I18n.getText("jrole-group-usercf.share_project_role"), jQuery(proj).text(), jQuery(role).text());
    } else {
        textVal = AJS.format(AJS.I18n.getText("jrole-group-usercf.share_project"), jQuery(proj).text());
    }

    var newElem = "<div id='" + prId + "'><span>" + textVal + "</span></div>";
    jQuery("#share_display_div").append(newElem);
    jQuery("#share_trash_sh").clone().show().appendTo("#" + prId);
}

function removeGroup(event) {
    var source = event.target || event.srcElement;
    var parent = jQuery(source).parent();
    var parentId = jQuery(parent).attr("id");

    var sharesObj = jQuery.evalJSON(jQuery("#shares_data").val());
    for (var objId in sharesObj) {
        if (sharesObj[objId]["id"] == parentId) {
            sharesObj.splice(objId, 1);
        }
    }
    jQuery("#shares_data").val(jQuery.toJSON(sharesObj));
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
//<--

