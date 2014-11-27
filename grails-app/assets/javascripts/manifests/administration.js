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
 
// This is a manifest file that'll be compiled into administration.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery-ui/jquery-ui-1.10.3.custom.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.min.js
//= require jquery-plugins/dataTables/jquery.dataTables.ajax.reloader.js
//= require jquery-plugins/multi-select/jquery.multi-select.js
//= require jquery-plugins/timepicker/jquery-timepicker.js
//= require jquery-plugins/timepicker/jquery-ui-timepicker-addon.js
//= require_self

/**
 * Global administration setting params
 */
var adminSettings = {};

/**
 * Stores the original form if an subform exists.
 */
var originalForms = new Array();

/**
 * XMLHttpRequest to open an post request
 */ 
var xmlHttpRequest;

/**
 * Shows an password field in an user formular.
 */
function showPasswordField() {
    $.get(adminSettings.url.template, {name: 'Password'},
            function(data) {
                if (typeof data === 'object') {
                    $(messageContainer).empty();
                    $(messageContainer).append(message(messageType.error, data.message));
                } else {
                    var div = $('#pwReset');
                    div.empty();
                    div.append(data);
                }
            });
    return false;
}

/**
 * Method to open an create or update formular in the administration view.
 * 
 * @param {boolean} isEdit indicator if create (false) or update(true) formular should be loaded
 * @param {type} id identity from the entity that should be edit
 */
function openForm(isEdit, id) {
    var url;
    var params;
    
    if($.isEmptyObject(adminSettings)) {
        return false;
    }

    // Open create or update formular
    if (isEdit === false) {
        url = adminSettings.url.create;
    } else {
        url = adminSettings.url.update;
        params = {
            id: id
        };
    }

    $.get(url, params)
            .done(function(data) {
                if (data.statusCode) {
                    $(messageContainer).prepend(message(messageType.error, data.message));
                    refreshDataTable(adminSettings.dt, adminSettings.category);
                } else {
                    var formular;
                    var formContainer = $('#' + adminSettings.formContainer);
                    var adminContainer = $('#' + adminSettings.adminContainer);
                    formContainer.empty();
                    formContainer.append(data);
                    formContainer.css("display", "none");
                    formular = formContainer.find('form');
                    $(formular).foundation('abide');
                    switchAnimation(adminContainer, formContainer);
                }
            })
            .fail(function(xhr) {
                $(messageContainer).empty();
                $(messageContainer).append(messageService.createErrorMessage(xhr.responseText));
            });
            
    return false;
}

/**
 * Switch an second formular to his current form
 * 
 * @param {type} button img is clicked
 * @param {type} url to an controller
 * @param {type} isOpen is second form open (true) or not (false)
 * @param {type} iconSrc icon source to change it
 * @param {type} formular to append
 * @param {type} initUpload if an uploader should be init after append
 */
function switchForm(button, url, isOpen, iconSrc, formular, initUpload) {
    var currentSource = $(button).attr("src");
    if (!isOpen) {
        originalForms[$(formular).attr("id")] = $(formular).clone();
        $(button).removeAttr("onclick");
        $(button).unbind("click");
        $(button).click(function() {
            switchForm(button, url, true, currentSource, formular);
        });
        $(button).attr("src", iconSrc);
        $.get(url, function(data) {
            formular.empty();
            formular.append(data);

            if (initUpload && initUpload === 'fileSample') {
                $('#fileUploaderSample').uploader({
                    id: "#fileSample"
                });
            }

            if (initUpload && initUpload === 'fileSensor') {
                $('#fileUploaderSensor').uploader({
                    id: "#fileSensor"
                });
            }

            if (initUpload && initUpload === 'fileVM') {
                $('#fileUploaderVM').uploader({
                    id: "#fileVM"
                });
            }
        });
        adminSettings[$(formular).attr("id")] = formular;
    } else {
        formular.empty();
        formular.append(originalForms[$(formular).attr("id")]);
        $(button).removeAttr("onclick");
        $(button).unbind("click");
        $(button).click(function() {
            switchForm(button, url, false, currentSource, formular);
        });
        $(button).attr("src", iconSrc);
    }
}

/**
 * Aborts an XMLHttpRequest if is running.
 */
function abortXMLRequest() {
    if (xmlHttpRequest instanceof XMLHttpRequest) {
        xmlHttpRequest.abort();
    }
    return false;           
}

// JQuery extension
(function($) {

    /**
     * Indicator for an selected button in the administration view.
     */
    var activeButton;
    
    /**
     * Indicator for an running upload.
     */
    var isUploadInProgress = false;
    
    /**
     * Inits an administration view to his div.
     * @param {Object} options to configuration the view
     */
    $.fn.initAdminView = function(options) {
        
        if($(this).html() !== $(activeButton).html()) {
                        
            switchAnimation($('#' + adminSettings.formContainer), $('#' + adminSettings.adminContainer));
            
            adminSettings = $.extend({
                adminContainer: "admin-container",
                dataTableContainer: "admin-dt",
                formContainer: "admin-form",
                isFormOpen: false
            }, options);
                        
            $('#' + adminSettings.dataTableContainer).empty();
            $.get(adminSettings.url.initDataTable, function(config) {
                $.extend(config, generateDefaultMap(adminSettings.category));
                adminSettings.dt = createDataTable($('#' + adminSettings.dataTableContainer), config);
            });
                        
            if (activeButton !== undefined) {
                $(activeButton).css("background-color", "");
            }

            $(this).css("background-color", "#951D40");
            activeButton = $(this);
        }
    };

    /**
     * An foundation plugin to check formular data of his validation and then
     * sends it to the response server.
     * 
     * @author Andreas Sekulski
     * @param {String} options to his response server.
     */
    $.fn.sendFormData = function(options) {
        // Default Settings
        var settings = $.extend({
            formular: $(this),
            uploader: '#uploader',
            url: ""
        }, options);

        /** Append valid event to an selected formular **/
        $(settings.formular).on('valid', function() {
            if (isUploadInProgress === false) {
                isUploadInProgress = true;
                sendData(settings.formular);
            }
            return false;
        });
        
        /** Append invald event to an selected formular **/
        $(settings.formular).on('invalid', function() {
            $(settings.formular).foundation();
            return false;
        });
        
        /**
         * Sends formular data to an server
         * @param {Object} formular to send
         */
        function sendData(formular) {
            // Total filesize in bytes
            settings.totalFilesize = 0;
            // Create an formdata object to send it to an controller
            var formData = generateFormData(formular);
            // Create an new data request
            xmlHttpRequest = new XMLHttpRequest();

            $(settings.uploader).show();
            hideShowElement('#sendData', '#uploadButtons');
            $(settings.uploader).find(".progress").css('width', 0 + "%");
            
            xmlHttpRequest.onerror = function(e) {
                $(messageContainer).prepend(message(messageType.error, e.responseText));
                finishUpload();
            };

            xmlHttpRequest.onload = function() {
                var json = $.parseJSON(this.response);
                $(settings.uploader).find(".meter").html("100%");
                if (json.version) {
                    $($(formular).find('input:hidden')[1]).val(json.version);
                }

                if (json.statusCode === 200) {
                    openForm(json.isUpdate, json.id);
                    $(messageContainer).prepend(message(messageType.success, json.message));
                    refreshDataTable(adminSettings.dt, adminSettings.category);
                } else if (json.statusCode !== 200) {
                    $(messageContainer).prepend(message(messageType.error, json.message));
                }

                finishUpload();
            };

            xmlHttpRequest.upload.onprogress = function(e) {
                var p = Math.round(100 / settings.totalFilesize * e.loaded);
                if (p === Infinity || p >= 100) {
                    p = 100;
                    $('#uploadButtons').hide();
                }
                $(settings.uploader).find(".meter").html(p + "%");
                $(settings.uploader).find(".progress").css('width', p + "%");
            };

            xmlHttpRequest.onabort = function() {
                finishUpload();
            };

            xmlHttpRequest.open("POST", settings.url);
            xmlHttpRequest.send(formData);
        }
        
        /**
         * Function if an upload is finished
         */
        function finishUpload() {
            $(settings.uploader).hide();
            hideShowElement('#uploadButtons', '#sendData');  
            isUploadInProgress = false;
            refreshDataTable(adminSettings.dt, adminSettings.category);
        }
        
        /**
         * Creates an form data object to send all inputs.
         * @param {Object} formular
         * @returns {FormData}
         */
        function generateFormData(formular) {
            var formData = new FormData();
            
            $.each($(formular).serializeArray(), function(index, data) {
                formData.append(data.name, data.value);
            });

            $.each($(formular).find('input:file'), function(index, input) {
                $.each(input.files, function(index, file) {
                    settings.totalFilesize = settings.totalFilesize + file.size;
                    formData.append(input.id, file);
                });
            });
            
            return formData;
        }
        
        return false;
    };

    /**
     * An foundation plugin to create an input type file element with an form 
     * validation.
     * 
     * @param {Object} options to configure this input file element.
     */
    $.fn.uploader = function(options) {

        var settings = $.extend({
            id: "fileuploader"
        }, options);
        
        settings.fileInformation = {
            name: settings.id + 'name',
            size: settings.id + 'size',
            type: settings.id + 'type'
        };

        $(settings.id).change(function() {
            $(settings.fileInformation.name).html(this.files[0].name);
            $(settings.fileInformation.size).html(bytesToSize(this.files[0].size));
            $(settings.fileInformation.type).html(this.files[0].type);
        });

        /**
         * Function to calc bytes to an readable size.
         * 
         * @param {Number} bytes
         * @returns {Number|String} gets an calculate size and measurment.
         */
        function bytesToSize(bytes) {
            var units = [' bytes', ' KB', ' MB', ' GB', ' TB', ' PB', ' EB', ' ZB', ' YB'];
            var amountOf2s = Math.floor(Math.log(+bytes) / Math.log(2));
            if (amountOf2s < 1) {
                amountOf2s = 0;
            }
            var i = Math.floor(amountOf2s / 10);
            bytes = +bytes / Math.pow(10, (i + 1) * 2);

            if (bytes.toString().length > bytes.toFixed(1).toString().length) {
                bytes = bytes.toFixed(1);
            }

            return bytes + units[i];
        }
    };

    /**
     * Shows an delete dialog in his dialog content.
     * 
     * @param {Object} options params to configure delete dialog
     */
    $.fn.showDeleteDialog = function(options) {
        $.get(options.url.modal, function(data) {
            var modal = createModal(data);
            $(modal).addClass('tiny');
            $(modal).foundation('reveal', 'open');
            
            $('#dButtonYes').click(function() {
                deleteItem(modal, options.url.delete);
                closeDialog(modal);
                return false;
            });

            $('#dButtonNo').click(function() {
                closeDialog(modal);
                return false;
            });
            
        }).fail(function(error) {
            $(messageContainer).prepend(message(messageType.error, error.responseText));
        });

        function deleteItem(dialog, url) {
            $.get(url, function(data) {
                $(messageContainer).prepend(message(messageType.success, data));
                refreshDataTable(adminSettings.dt, adminSettings.category);
            }).fail(function(error) {
                refreshDataTable(adminSettings.dt, adminSettings.category);
                $(messageContainer).prepend(message(messageType.error, error.responseText));
            });

            closeDialog(dialog);
        }

        function closeDialog(modal) {
            $(modal).foundation('reveal', 'close');
            $(modal).remove();
        }
    };
}(jQuery));

/**
 * Document ready function
 */
$(document).ready(function() {
    $('#navAdmin').addClass('active');
    $(document).bind('keydown', function(e) {
        if (e.which === 116) {
            refreshDataTable(adminSettings.dt, adminSettings.category);
            return false;
        }
    });
    
    scale();
    $( window ).resize(function() {
         scale();
    });
});

/**
 * Scale function for administration view
 */
function scale() {
    $('#adminContainer').height($('#container').height() - $('#top-bar').outerHeight(true) - $('#subNavAdmin').outerHeight(true) - $('#footer').outerHeight(true))
}