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

/**
 * Javascript file to init the create.gsp view.
 * 
 * @author  Andreas Sekulski
 **/

/**
 * Default configuration of an multiselect field.
 * @type Object
 **/
var defaultMultiselect = {
    afterSelect: function(values){
        $(document).foundation();
    },
    afterDeselect: function(values){
        $(document).foundation();
    }
};

/**
 * Init function for create formular for an job.
 */
function init() {
    $('#vm-hypervisor-multi').multiSelect($.extend({selectableOptgroup: true}, defaultMultiselect));  
    $('#sensor-multi').multiSelect(defaultMultiselect);
    $('#sample-multi').multiSelect(defaultMultiselect);
    $('#jobTimeout').timepicker();
    $('#jobReboot').timepicker();
    $('#jobChooseDate').datetimepicker(); 
    $('#jobEarlyStartDate').datetimepicker(); 
    $(document).foundation();
}

/** Document ready init function **/
$(function() {
    window.setTimeout(init, 500);
});