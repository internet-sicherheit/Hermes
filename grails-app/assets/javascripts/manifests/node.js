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

// This is a manifest file that'll be compiled into node.js.
// 
// @author  Andreas Sekulski
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery-plugins/flot/jquery.flot.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.ajax.reloader.js
//= require_self

/**
 * Configuration from view contains all settings.
 */
var settings;

/**
 * Document ready function
 */
$(document).ready(function() {
    $('#navNode').addClass('active');
    scale();
    $(window).resize(function() {
         scale();
    });
});


/**
 * Scale function for node view
 */
function scale() {
    $('#node-container').height(
            $('#container').height() 
            - $('#top-bar').outerHeight(true) 
            - $('#footer').outerHeight(true));
}

/**
 * Inits multiaxes plotter
 * 
 * @param id Identity from node
 */
function initPlotter(id) {

    settings.plotterData = [];
    settings.nodeID = id;

    $.get(settings.url.initPlotter, {id: settings.nodeID, max: settings.totalPoints})
            .done(function(data) {

                for (var key in settings.avg) {

                    if (data[key].length < settings.totalPoints) {
                        while (settings.avg[key].length < settings.totalPoints - data[key].length) {
                            settings.avg[key].push(0);
                        }
                    }

                    settings.avg[key] = settings.avg[key].concat(data[key]);

                    settings.plotterData.push(settings.avg[key]);
                }

                $.plot(settings.container.plotter, settings.plotterData, settings.plotter);
                settings.drawPlotter = true;
            })
            .fail(function(xhr) {
                $(messageContainer).append(message(messageType.error, xhr.responseText));
                settings.drawPlotter = false;
                hidePlotter();
            });
}

/**
 * Gets an array of plot data to draw.
 * 
 * @param key Key Identity from settings graph
 * @param label Label identification from graph
 * @param value Value from graph
 * 
 * @returns {Array} array of plot data
 */
function getPlotterData(key, label, value) {
    var res = [];

    if (settings.avg[key].length > 0) {
        settings.avg[key] = settings.avg[key].slice(1);
    }

    while (settings.avg[key].length < settings.totalPoints) {
        settings.avg[key].push(value);
    }

    for (var i = 0; i < settings.avg[key].length; ++i) {
        res.push([i, settings.avg[key][i]]);
    }

    return {
        data: res, label: label
    };
}

/**
 * Updates the current plotter if exists
 */
function updatePlotter() {
    $.get(settings.url.plotter, {id: settings.nodeID})
            .done(function(data) {

                settings.plotterData = [];
                for (var key in settings.avg) {
                    var avg = data.avg[key];
                    settings.plotterData.push(getPlotterData(key, avg.label, avg.value));
                }

                $.plot(settings.container.plotter, settings.plotterData, settings.plotter);

                updateDetailView(data.node);
            })
            .fail(function(xhr) {
                $(messageContainer).append(message(messageType.error, xhr.responseText));
                settings.drawPlotter = false;
                hidePlotter();
            });
}

/**
 * Updates an detail view from an selected node
 * 
 * @param node Node JSON
 */
function updateDetailView(node) {
    for (var key in node) {
        $('#' + key).empty();
        $('#' + key).append(node[key]);
    }
}

/**
 * Draws in an plotter an node graph with the given id.
 * 
 * @param id Node ID how should be drawn
 */
function drawNodePlot(id) {
    settings.drawPlotter = false;
    showPlotter(id);
}

/**
 * Shows an plotter
 * @param id Identity from Node to show an plotter
 */
function showPlotter(id) {
    $(settings.container.dt).fadeOut("slow", function() {
        $(settings.container.detail).fadeIn("slow", function() {
            if (!settings.nodeID || settings.nodeID !== id) {
                initPlotter(id);
            } else {
                settings.drawPlotter = true;
            }
        });
    });
}

/**
 * Hides current opend plotter and shows the data table view.
 */
function hidePlotter() {
    $(settings.container.detail).fadeOut("slow", function() {
        $(settings.container.dt).fadeIn();
        settings.drawPlotter = false;
    });
}

/**
 * Append JQuery functions
 */
(function($) {

    /**
     * Inits the node view page
     * 
     * @param {Object}
     *            options Init options parameters
     */
    $.fn.initNodeView = function(options) {

        /* Default settings configuration */
        settings = $.extend({
            container: {
                dt: '#dt_table',
                plotter: '#realchart',
                detail: '#node_detail'
            },
            avg: {
                avg1: [], avg5: [], avg15: []
            },
            ticker: 2500,
            plotterData: [],
            drawPlotter: false,
            nodeID: 0,
            plotter: {
                series: {shadowSize: 0},
                xaxes: [{show: false}],
                yaxes: [{min: 0, max: 100}],
                legend: {position: "nw"}
            }
        }, options);

        /* Datatable creation */
        $.get(settings.url.dtInit, function(config) {
            settings.dt = createDataTable(settings.container.dt, config);
        });
        
        /* Configuration data from plotter */
        settings.totalPoints = 300;
        settings.plotData = [];

        /**
         * Intervall init to refresh data
         */
        self.setInterval(function() {
            if (settings.drawPlotter) {
                updatePlotter();
            } else {
                refreshDataTable(settings.dt);
            }
        }, settings.ticker);

        $(settings.container.detail).hide();
    };
}(jQuery));