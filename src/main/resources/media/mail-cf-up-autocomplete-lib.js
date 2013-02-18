/* Created by Dmitry Miroshnichenko 11-02-2013. Copyright Mail.Ru Group 2013. All
 rights reserved. */

// constants here
var upCfValsMethod = 'getcfvals';
var upUserHtmlMethod = 'getuserhtml';
var upRepresentationTagPostfix = '-userpicker-representation';
var UP_MAX_DISPLAY_ROWS = 20;
var UP_AUTOCOMPLETE_STARTS_AFTER = 2;
var PREVENT_DEFAULT_FLAG = false; // firefox has problems with preventDefault

(function($) {
	$.fn.upcfautocomplete = function(cfId, issueId, baseUrl, restUrlPart,
			minlength) {
		callback = typeof minlength == "function" ? minlength
				: (typeof callback == "function" ? callback : function() {
				});
		minlength = !isNaN(Number(minlength)) ? minlength
				: UP_AUTOCOMPLETE_STARTS_AFTER;
		var getValsUrl = baseUrl + restUrlPart + upCfValsMethod;
		var getIssueHtmlUrl = baseUrl + restUrlPart + upUserHtmlMethod;
		var input = this;
		input[0].lastSelectedValue = input.val();

		var listDiv = $(document.createElement("div"));

		listDiv.css({
			top : "24px"
		});
		listDiv.addClass("suggestions");

		this.after(listDiv);

		listDiv.hide();
		function hideDropDown() {
			listDiv.hide();
			$(document).unbind("click", hideDropDown);
		}
		function suggest() {
			var currentTextfieldValue = input.val();

			var lastQuery = input[0].lastQuery ? input[0].lastQuery
					.toLowerCase() : "";
			var lastSelectedValue = input[0].lastQuery ? input[0].lastSelectedValue
					.toLowerCase()
					: "";
			if (currentTextfieldValue.length >= minlength
					&& currentTextfieldValue.trim().toLowerCase() != lastQuery
					&& currentTextfieldValue.trim().toLowerCase() != lastSelectedValue) {

				jQuery
						.ajax({
							url : getValsUrl,
							type : "POST",
							dataType : "json",
							data : {
								"cf_id" : cfId,
								"issue_id" : issueId,
								"pattern" : currentTextfieldValue,
								"rowcount" : UP_MAX_DISPLAY_ROWS + 1
							// we need to know if there are more elements to
							// announce user
							},
							async : false,
							error : function(xhr, ajaxOptions, thrownError) {
								handleError(xhr, ajaxOptions, thrownError)
							},
							success : function(data) {
								var html = "<div class=\"aui-list\">";
								currentTextfieldValue = currentTextfieldValue
										.trim().toLowerCase();
								if (data != null) {

									var listBody = "<ul class=\"aui-list-section suggestions_ul_"
											+ cfId + "\">";

									var displayRowsCount = 0;
									var i = 0;
									while (i < data.length
											&& displayRowsCount < UP_MAX_DISPLAY_ROWS) {

										var obj_name = data[i].name;
										var obj_type = data[i].type;
										var obj_typeimage = data[i].typeimage;
										var obj_descr = data[i].description;
										var obj_state = data[i].state;
										var obj_stateimage = data[i].stateimage;
										var obj_preference = data[i].preference;
										var obj_preferenceimage = data[i].preferenceimage;

										var searchableData = obj_name;
										var tag_title = obj_name;
										if (obj_descr) {
											searchableData += " - " + obj_descr;
											tag_title += " - " + obj_descr;
										}

										var startIndex = searchableData
												.toLowerCase().indexOf(
														currentTextfieldValue);
										if (startIndex != -1) {
											displayRowsCount++;
											var dataToList = "";

											var lastIndex = startIndex;
											var dataPos = 0;
											while (lastIndex != -1) {
												dataToList += searchableData
														.substring(dataPos,
																lastIndex)
														+ "<b>"
														+ searchableData
																.substring(
																		lastIndex,
																		lastIndex
																				+ currentTextfieldValue.length)
														+ "</b>";
												dataPos = lastIndex
														+ currentTextfieldValue.length;
												lastIndex = searchableData
														.indexOf(
																currentTextfieldValue,
																dataPos);
											}
											if (dataPos < searchableData.length) {
												dataToList += searchableData
														.substring(
																dataPos,
																searchableData.length);
											}
											var stylePart = '';
											if (obj_typeimage) {
												stylePart = "style=\"background-image: url("
														+ obj_typeimage
														+ "); text-overflow: ellipsis; overflow: hidden;\"";
											}
											var statePart = '';
											if (obj_state) {
												statePart = " - <i>"
														+ obj_state + "</i>";
											}

											listBody += "<li class=\"aui-list-item\""
													+ " li-cf-value-"
													+ cfId
													+ "=\""
													+ obj_name
													+ "\">"
													+ "<a class=\"aui-list-item-link aui-iconised-link\" href=\"#\""
													+ stylePart
													+ "title=\""
													+ tag_title
													+ "\">"
													+ dataToList
													+ statePart
													+ "</a>" + "</li>";

										}
										i++;
									}
									var listHeader = "";
									if (displayRowsCount > 0) {
										// TODO revise i18n in Jira 4.3
										listHeader = "<h5>"
												+ /* upCfHistorySearchHint */"History Search"
												+ "<span class=\"aui-section-description\"> ("
												+ /* upCFShowingElementsHint */"Showing "
												+ displayRowsCount
												+ /* upCFMatchingElementsHint */" matching elements.";
										if (i < data.length) {
											listHeader += /* upCfMoreElemsHint */" There are more elements."
													+ ")</span></h5>";
										} else {
											listHeader += ")</span></h5>";
										}
									}

									listBody += "</ul>";
								}
								html += listHeader + listBody + "</div>";
								listDiv.html(html);
								$("li", listDiv)
										.click(
												function(e) {
													e.stopPropagation();
													var value = this
															.getAttribute("li-cf-value-"
																	+ cfId);
													select(value);
												}).hover(function() {
											$(".active").removeClass("active");
											$(this).addClass("active");
										}, function() {
										});

								$(document).click(hideDropDown);

								listDiv.show();
							}
						});

				input[0].lastQuery = currentTextfieldValue;
			} else if (currentTextfieldValue.length < minlength) {
				hideDropDown();
			}
		}
		;
		input.keydown(function(e) {
			var that = this;
			if (this.timer) {
				clearTimeout(this.timer);
			}
			var actions = {
				"40" : function() { // down key
					var li = $(".suggestions_ul_" + cfId + " .active")
							.removeClass("active").next();
					if (li.length) {
						li.addClass("active");
					} else {
						$(".suggestions_ul_" + cfId + " li:first").addClass(
								"active");
					}
					return !PREVENT_DEFAULT_FLAG;
				},
				"38" : function() { // up key
					var li = $(".suggestions_ul_" + cfId + " .active")
							.removeClass("active").prev();
					if (li.length) {
						li.addClass("active");
					} else {
						$("li:last", listDiv).addClass("active");
					}
					return !PREVENT_DEFAULT_FLAG;
				},
				"27" : function() { // escape key
					hideDropDown();
					return PREVENT_DEFAULT_FLAG;
				},
				"13" : function() { // enter key
					var obj = $(".suggestions_ul_" + cfId + " .active")[0];
					if (obj) {
						select(obj.getAttribute("li-cf-value-" + cfId));
					}
					return PREVENT_DEFAULT_FLAG;
				},
				"9" : function() { // tab key
					this[13]();
					setTimeout(function() {
						that.focus();
					}, 0);
					return !PREVENT_DEFAULT_FLAG;
				}
			};
			var actionRes = true;
			if (listDiv.css("display") != "none" && e.keyCode in actions) {
				e.preventDefault(); // doesn't works in firefox
				actionRes = actions[e.keyCode]();
			}
			this.timer = setTimeout(suggest, 100);
			return actionRes;
		});

		function select(value) {
			if (value) {
				jQuery
						.ajax({
							url : getIssueHtmlUrl,
							type : "POST",
							dataType : "json",
							data : {
								"cf_id" : cfId,
								"cf_value" : value
							},
							async : true,
							error : function(xhr, ajaxOptions, thrownError) {
								handleError(xhr, ajaxOptions, thrownError)
							},
							success : function(data) {
								// customization for different fields
								if ($('.' + cfId + upRepresentationTagPostfix).length > 0) {
									if (data) {
										var representation = $('.' + cfId
												+ upRepresentationTagPostfix);
										if (representation.length == 1) {
											if (representation.children('#'
													+ 'internal-' + cfId + '_'
													+ value).length <= 0) {
												representation
														.html(representation
																.html()
																+ data.html);
												var multiSelect = $('#' + cfId);
												multiSelect
														.html(multiSelect
																.html()
																+ "<option id=\""
																+ cfId
																+ "_"
																+ value
																+ "\" class=\"mail-multiuserpicker-element\" value=\""
																+ value
																+ "\" selected=\"selected\">"
																+ value
																+ "</option>");
											}
											input.val('');
											input[0].lastSelectedValue = '';
											input[0].defaultValue = '';
											input.trigger('input');
										}
									} else {
										// do nothing
									}
								} else {
									input.val(value);
									input[0].lastSelectedValue = value;
									input[0].defaultValue = value;
									input.trigger('input');
								}
							}
						});

				hideDropDown();
			}
		}

		function handleError(xhr, ajaxOptions, thrownError) {
			try {
				var respObj = eval("(" + xhr.responseText + ")");
				initErrorDlg(respObj.message).show();
			} catch (e) {
				initErrorDlg(xhr.responseText).show();
			}
		}

	};
})(jQuery);

function removeDependantUser(cfId, user) {
	var element = jQuery('#internal-' + cfId + '_' + user);

	if (element.length == 1) {
		element.remove();
	}

	var associatedOption = jQuery('#' + cfId + '_' + user);

	if (associatedOption.length == 1) {
		associatedOption.remove();
	}
};

function setDependatUsers(restUrl, cfId, user) {
	jQuery.ajax({
		url : restUrl + upUserHtmlMethod,
		type : "POST",
		dataType : "json",
		data : {
			"cf_id" : cfId,
			"cf_value" : user
		},
		async : false,
		error : function(xhr, ajaxOptions, thrownError) {
			handleError(xhr, ajaxOptions, thrownError)
		},
		success : function(data) {
			if (jQuery('.' + cfId + upRepresentationTagPostfix).length > 0) {
				if (data) {
					var representation = jQuery('.' + cfId
							+ upRepresentationTagPostfix);
					if (representation.length == 1) {
						representation.html(representation.html() + data.html);
					}
				}
			}
		}
	});
};
