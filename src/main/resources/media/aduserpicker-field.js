// Created by Andrey Markelov 11-11-2012.
// Copyright Mail.Ru Group 2012. All rights reserved.

//--> set values from multi select to hidden input
function mail_ru_change_multu_user_picker(cfId) {
    var str = "";
    AJS.$("#mail_ru_select_" + cfId + " option:selected").each(function () {
        if (str != "") {
            str += ", ";
        }
        str += AJS.$(this).val();
    });
    AJS.$("#" + cfId).val(str);
}
