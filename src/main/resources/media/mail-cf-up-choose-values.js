/* Created by Dmitry Miroshnichenko 12-02-2013. Copyright Mail.Ru Group 2013. All
 rights reserved.
 */
var upAutocompleteRestUrl = "/rest/upautocompletesrv/1.0/upautocompletesrv/";

var upCfHistorySearchHint = AJS.I18n
		.getText("jrole-group-usercf.autocomp.js.historysearch");
var upCfMoreElemsHint = AJS.I18n
		.getText("jrole-group-usercf.autocomp.js.moreelems");
var upCFShowingElementsHint = AJS.I18n
		.getText("jrole-group-usercf.autocomp.js.showingelems");
var upCFMatchingElementsHint = AJS.I18n
		.getText("jrole-group-usercf.autocomp.js.matchingelems");

jQuery(document).ready(function() {
	// JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function(e, context) {

	upManageAutocompleteFields('.cf-single-up-autocomplete');
	upManageAutocompleteFields('.cf-multiuserpicker-autocomplete');

	initDependantUsersList('.mail-user-picker-elements');

	// });
});

function upManageAutocompleteFields(classname) {
	var elems = jQuery(classname);

	if (elems.length > 0) {
		for ( var i = 0; i < elems.length; i++) {
			var element = AJS.$("#" + elems[i].getAttribute("input-id"));
			if (!element.hasClass('gr-autocomplete-inited')) {
				element.addClass("gr-autocomplete-inited");
				element.upcfautocomplete(elems[i].getAttribute("cf-id"),
						elems[i].getAttribute("issue-id"), elems[i]
								.getAttribute("cf-base-url"),
						upAutocompleteRestUrl, 2);
			}
		}
	}
};

function initDependantUsersList(classname) {
	var elems = jQuery(classname);

	if (elems.length > 0) {
		for ( var i = 0; i < elems.length; i++) {
			var cfId = elems[i].getAttribute("cf-id");
			var restUrl = elems[i].getAttribute("base-url")
					+ upAutocompleteRestUrl;
			var userOptions = jQuery('#' + cfId).children(
					'.mail-multiuserpicker-element');
			if (userOptions.length > 0) {
				for ( var j = 0; j < userOptions.length; j++) {
					setDependatUsers(restUrl, cfId, userOptions[j]
							.getAttribute("value"));
				}
			}
		}
	}
};

function chooseValFromNewWindow(cfId, baseUrl, returnCfId) {
	var marginTop = 100;
	var marginLeft = 500;

	var pickerWindow = window
			.open(
					baseUrl
							+ "/secure/popups/MailRuUserPickerValuePickerAction.jspa?cfid="
							+ cfId + "&inputid=" + cfId + "&returnid="
							+ returnCfId,
					AJS.I18n.getText("jrole-group-usercf.userpicker.title"),
					"status=0,toolbar=0,location=0,menubar=0,directories=0,resizable=1,scrollbars=1,height=500,width=500");

	pickerWindow.moveTo(marginLeft, marginTop);
};