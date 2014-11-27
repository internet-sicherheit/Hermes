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

// This is a manifest file that'll be compiled into jobstatus.js.
// 
// @author  Andreas Sekulski
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery-ui/jquery-ui-1.10.3.custom.min.js
//= require jquery-plugins/dynatree/jquery.dynatree.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.ajax.reloader.js
//= require_self

/**
 * Global Settings from jobstatus
 * @type type
 */
var globalSettings = {};

/**
 * Identity from div element from data table
 * @type String
 */
var divDataTableID = "dt_";

/**
 * Identity from an datatable
 * @type String
 */
var dataTableId = "dt_table_";

/**
 * Identity from an checkbox
 * @type String
 */
var checkBoxID = "dt_checkbox_";

/**
 * Update intervall in jobstatus view
 * @type Number
 */
var timer = 5000;

/**
 * Document ready function
 */
$(document).ready(function() {
    $('#navJobstatus').addClass('active');
    scale();
    $( window ).resize(function() {
         scale();
    });
});

/**
 * Scale function for jobstatus view
 */
function scale() {
    $('#publishJobNav').height(
            $('#container').height() 
            - ($('#publishJobNav').innerWidth() - $('#publishJobNav').width())
            - $('#top-bar').outerHeight(true) 
            - $('#filterNav').outerHeight(true) 
            - $('#refreshJobsButton').outerHeight(true) 
            - $('#footer').outerHeight(true));
}

/**
 * Inits an data table into the current view
 * 
 * @param {Boolean} isFilter how an filter view should be rendered
 */
function initDataTable(isFilter) {
    var url = "";
    
    if (globalSettings.dtType) {
        destroyTable(globalSettings.dtType);
        globalSettings.dtType = undefined;
    }

    if (isFilter === false) {
        $("#filterSection").fadeOut();
        url = globalSettings.url.allPublishedJob;
    } else {
        $("#filterSection").fadeIn();
        url = globalSettings.url.addJobs;
    }
            
    $.get(globalSettings.url.dtInit, function(config) {
        $.extend(config, {
            sAjaxSource: url, 
            fnServerParams: function(aoData) {
                aoData.push({"name": "type", "value": globalSettings.clickedType});
                aoData.push({"name": "nodeSelection", "value": globalSettings.filter});
            }
        });
        globalSettings.dtType = createDataTable('#publishJobNav', config, dataTableId);
    });
}

/**
 * Shows an specific data table with an given published job type.
 * 
 * @param {Object} button that triggers the event
 * @param {Boolean} isFilter if filter mode should be enabled
 * 
 */
function showPublishedJobs(button, isFilter) {
    var dd = $(button).parent();
    var dl = $(dd).parent();
    var active = $(dl).find('.active').removeClass('active');
    $(active).removeClass('active');
    $(dd).addClass('active');
    globalSettings.clickedType = $(button).html();
    initDataTable(isFilter);
    return false;
}

/**
 * Destroys an data table if exists and his container.
 * 
 * @param {Object} dataTable to delete.
 */
function destroyTable(dataTable) {
    if (dataTable !== undefined) {
        var container = $(dataTable).data('container');
        dataTable.fnDestroy(true);
        $(container).remove();
    }
}

/**
 * Open an modal window and shows an detail view from an job.
 * 
 * @param {type} id from an job
 */
function openJobDetail(id) {
    $.get(globalSettings.url.showJobDetail, {id: id})
            .done(function(data) {
                var modal = createModal($("<div/>").append(data));
                $("#body").append(modal);
                modal.foundation('reveal', 'open');
            })
            .fail(function(xhr) {
                $(messageContainer).empty();
                $(messageContainer).append(message(messageType.error, xhr.responseText));
            });
    return false;
}

/**
 * Open an modal window and shows an logfile from an job.
 * 
 * @param {type} id from an job
 */
function showLog(id) {
    $.get(globalSettings.url.showLogfile, {id: id})
            .done(function(data) {
                var modal = createModal($("<div/>").append(data));
                $("#body").append(modal);
                modal.foundation('reveal', 'open');
            })
            .fail(function(xhr) {
                $(messageContainer).empty();
                $(messageContainer).append(message(messageType.error, xhr.responseText));
            });
}

/**
 * Refreshed the current tree.
 */
function refreshTree() {
    var tree = globalSettings.tree;

    var openNodes = [];

    $(tree.selector).dynatree("getRoot").visit(function(node) {
        node.data.key = node.data.key.replace(",", ".");
        if (node.bExpanded && !node.data.key.contains(".")) {
            openNodes.push(node.data.key);
        }
    });
    
    var selectedNodes = [];

    $(tree.selector).dynatree("getRoot").visit(function(node) {
        if (node.bSelected) {
            selectedNodes.push(node.data.key);
        }
    });

    $(tree.selector).dynatree("option", "initAjax", {
        url: $(tree.selector).dynatree("option", "initAjax").url,
        data: {openNodes: openNodes, selectedNodes: selectedNodes}
    });

    // Reload
    $(tree.selector).dynatree('getTree').reload();
    
    return false;
}

/**
 * Refreshed all open data tables.
 */
function refreshPublishedJobs() {
    refreshDataTable(globalSettings.dtType);
    return false;
}

// JQuery extension
(function($) {

    /**
     * Inits an jobstatus view to his div.
     * @param {Object} options to configuration the view
     */
    $.fn.initTree = function(options) {
        
        globalSettings.selectedNodes = [];
        globalSettings.filter = new Array();
        globalSettings.tree = this;
        globalSettings.activeJobs = new Array();
        globalSettings.url = options.url;
                
        initDataTable(true);
        
        $('#refreshTreeButton').click(function() {
            return refreshTree();
        });
               
        self.setInterval(function() {
            refreshPublishedJobs();
        }, timer);

        $(this).dynatree({
            checkbox: true,
            selectMode: 3,
            initAjax: {
                url: options.url.init
            },
            onClick: function(node, event) {
                if (node.getEventTargetType(event) === "title" ||
                        node.getEventTargetType(event) === "checkbox") {
                    node.toggleSelect();
                    getJobs();
                    return false;
                }
            },
            onKeydown: function(node, event) {
                if (event.which === 32) {
                    node.toggleSelect();
                    getJobs();
                    return false;
                }
            },
            onLazyRead: function(node) {
                node.appendAjax({
                    url: options.url.addChild,
                    data: {id: node.data.key, selected: node.bSelected}
                });
            }
        });

        /*
         * Get all Jobs from the selected filter and list it to an element.
         */
        function getJobs() {
            var selKeys = $.map($("#tree").dynatree("getSelectedNodes"), function(node) {
                node.data.key = node.data.key.replace(",", ".");
                return node.data.key;
            });
            globalSettings.filter = selKeys;
            refreshPublishedJobs();
        }
    };
}(jQuery));