/*
 * Copyright (c) 2013, 2014 Institute for Internet Security - if(is)
 *
 * This file is part of Hermes Malware Analysis System.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl 5
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
 
// This is a manifest file that'll be compiled into core.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery
//= require jquery/prototype/string.js
//= require foundation/foundation.min.js
//= require_self

/**
 * Set up a global AJAX error handler to handle the 401
 * unauthorized response. If a 401 status code comes back,
 * the user is no longer logged-into the system and can not
 * use it properly.
 */
$.ajaxSetup({
    statusCode: {
        401: function() {
            // Redirect to the login page.
            location.href = window.location.origin + "/Hermes/";
        }
    }
});

/**
 * Message container id to print out all messages.
 * @type String
 */
var messageContainer = "#message-container";

/**
 * Types of messages how can be generated
 */
var messageType = {
    success: 'success',
    error: 'alert',
    info: 'info'
};

/**
 * Document ready function
 */
$(function() {
    $(document).foundation();
});

/**
 * Method to generate message divs for Foundation5
 * 
 * @param {String} type messageType
 * @param {String} message
 * 
 * @return div message element
 */
this.message = function(type, message) {
    var settings = {
        animation: "slow",
        timer: 4000
    };

    var messageDiv = $('<div data-alert/>');
    messageDiv.attr({
        "class": "alert-box " + type + " nomargin"
    });

    $(messageDiv).append(message);

    messageDiv.delay(settings.timer).fadeOut(settings.animation, function() {
        $(this).remove();
    });

    return messageDiv;
};

/**
 * Generates the default parameters from all data tables.
 * 
 * @param {String} category
 * 
 * @returns an map with the default data tables configuration.
 */
function generateDefaultMap(category) {
    return {
        "fnInitComplete": function() {
            generateDetailView(this, category);
        }
    };
}

/**
 * Generates an detail view for an data table cell.
 * 
 * @param {DataTable} oTable an data table
 * @param {String} category that should be appen an detail view
 */
function generateDetailView(oTable, category) {
    if (oTable.fnSettings().aoData.length > 0 && adminSettings.detail) {
        var selector = oTable.selector;

        $(selector + ' tbody tr').each(function() {
            var detailButton = $("<a href=''>");
            $(detailButton).html('Detail');
            $(this).find('td:eq(0)').prepend("<br>");
            $(this).find('td:eq(0)').prepend(detailButton);
            
            $(detailButton).click(function() {
                var nTr = $(this).parents('tr')[0];
                if (oTable.fnIsOpen(nTr)) {
                    /* This row is already open - close it */
                    oTable.fnClose(nTr);
                } else {
                    /* Open this row */
                    oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr, category), 'details');
                }
                return false;
            });
            
        });
    }
}

/**
 * Generates the detail view for an data table. 
 * @param {DataTable} oTable an data table
 * @param {TableRow} nTr the give table row.
 * @param {String} category that should be appen an detail view
 * 
 * @returns {String} the given detail view output.
 */
function fnFormatDetails(oTable, nTr, category)
{
    var aData = oTable.fnGetData(nTr);
    var sOut = '';

    sOut = '<table cellpadding="5" cellspacing="0" border="0">';
    $.each(adminSettings.detail, function(index, value) {
        sOut += '<tr><td>' + value + '</td><td>'
            + aData[value] + '</td></tr>';
    });
    sOut += '</table>';
    return sOut;
}

/**
 * Refresh current data table.
 * 
 * @param {DOM} dataTable object
 * @param {String} category
 */
function refreshDataTable(dataTable, category) {
    if (dataTable) {
        dataTable.fnReloadAjax(null, function() {
            generateDetailView(dataTable, category);
        }, true);
    }
    return false;
}

/**
 * Creates an modal window
 * @param {type} data
 * @returns {jQuery}
 */
function createModal(data) {
    var dialog = $('<div data-reveal/>').attr({class: "reveal-modal"});
    $(dialog).append(data);
    $(dialog).append($("<a/>").attr({class: "close-reveal-modal"}).append("&#215;"));
    $(dialog).foundation('reveal');
    return dialog;
}

/**
 * Creates an data table and append its to the div container
 * 
 * @param {DOM} div Container to append
 * @param {JSON} parameters DataTable init parameters
 * @param {type} id Optional Identity from the data table
 * @returns {Object} An data table object
 */
function createDataTable(div, parameters, id) {
    var table = $('<table/>');  
    if(id) {
        $(table).attr("id", id);
    }
    $(div).append(table);
    return table.dataTable(parameters);    
}

/**
 * Switch animation to hide and show dom elements.
 * @param {Object} hide The hide element
 * @param {Object} showing The showing element
 */
function switchAnimation(hide, showing) {
    hide.fadeOut("medium", function() {
        showing.fadeIn();
    });
}

function hideShowElement(hide, showing) {
    $(hide).hide();
    $(showing).show();
}