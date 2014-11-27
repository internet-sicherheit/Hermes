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

package net.ifis.ites.hermes.management

import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import grails.converters.JSON
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

/**
 * Controller to handle all incoming requests for an Sample.
 * 
 * @author Andreas Sekulski
 **/
class SampleController {
    
    /**
     * Injected data table service
     **/
    def dataTableService
    
    /**
     * Injected factory to generate hypervisor params.
     **/
    def factoryParamsService

    /**
     * Dependency injection from administrationService service.
     **/
    def administrationService
    
    /**
     * Injected Management Service to save/update/delete entities
     **/
    def managementService
    
    /**
     * Injected page locator from groovy to check if an template or view exists
     **/
    def GrailsConventionGroovyPageLocator groovyPageLocator
    
    /**
     * Renders an create formular from an sample object.
     **/    
    def create() {
        return render(view : "Sample", 
            model:[
                instance : new Sample(),
                formTitel : message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [message(code: C.SAMPLE_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE),
                dataUploadRequired : true,
                url : createLink(controller:'Sample', action: 'save')])
    }
    
    /**
     * Renders an specific template if exists else an template not found error.
     * 
     * @param name Template name to render
     **/
    def template(String name) {
        if(!name || !groovyPageLocator.findTemplate(name)) {
            return render(message(code: C.DEFAULT_TEMPLATE_ERROR, args: [name]))  
        }
        
        return render(template : name, model: [params]) 
    }
    
    /**
     * Request parameters and convert it to his Sample params and try to
     * save it.
     **/
    def save() {
        StatusMessage responseJSON
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(Sample, params)
        
        if(!convertParams.file) {
            responseJSON = new StatusMessage()
            responseJSON.setMessage(g.message(code: C.DEFAULT_FILE_EXCEPTION))
            responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
            return render(responseJSON.getJSON())
        }
        
        responseJSON = administrationService.create(new Sample() , convertParams)
        return render(responseJSON.getJSON())
    }
    
    /**
     * Generates an edit view from an sample if an sample object exists
     *
     * @params id Identity number from an sample
     **/
    def edit(Long id) {
        StatusMessage responseJSON 
        Sample sample
        
        try {
            sample = administrationService.getEntity(Sample, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.SAMPLE_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
                    
        return render(view : "Sample", 
            model : [
                instance : sample,
                formTitel : message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [message(code: C.SAMPLE_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE),
                dataUploadRequired : false,
                url : createLink(controller:'Sample', action: 'update')
            ])      
    }
    
    /**
     * Request parameters and convert it to his Sample params and try to
     * update an sample.
     **/
    def update() {
        Sample sample
        StatusMessage responseJSON
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(Sample, params)
                
        try {
            sample = administrationService.getEntity(Sample, convertParams.id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.SAMPLE_LABEL_CODE), convertParams.id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
        
        // Checks if an user has update this hypervisor before
        responseJSON = administrationService.checkVersion(sample, convertParams.version)
        if(responseJSON) {
            return render(responseJSON.getJSON())
        }
        
        responseJSON = administrationService.update(sample, convertParams)
     
        return render(responseJSON.getJSON())
    }
    
    /**
     * Try to delete an existing sample
     * 
     * @params id Identity number from sample to delete
     **/
    def delete(Long id) {
        Sample sample
        StatusMessage responseJSON
        
        try {
            sample = administrationService.getEntity(Sample, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.SAMPLE_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
        
        if(Job.findAllBySample(sample).size() > 0) {
            responseJSON = new StatusMessage()
            responseJSON.setStatusCode(SEC.DATA_INTEGRATION_CODE)
            responseJSON.setMessage(g.message(
                    code: C.DEFAULT_DATA_INTEGRATION_ERROR_CODE, 
                    args: [g.message(code : C.SAMPLE_LABEL_CODE), sample]))    
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
                
        responseJSON = administrationService.delete(sample)
                
        return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
    }
    
    /**
     * Shows an modal window for an deletion for an sample.
     * 
     * @params id Identity number from an sample
     **/
    def deleteModal(Long id) {
        Sample sample
        StatusMessage responseJSON
        
        try {
            sample = administrationService.getEntity(Sample, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.SAMPLE_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }

        return render(template : "../administration/modal", 
            model : [
                message : g.message(code: C.DEFAULT_BUTTON_DELETE_MESSAGE, args: [g.message(code: C.SAMPLE_LABEL_CODE), sample.name])
            ])  
    }
    
    /**
     * Generates all sample data as an json data-table data.
     **/
    def dataTable() {
        HashMap aaData = new HashMap()
        List jsonSample = new ArrayList()
        
        for (sample in Sample.list()) {
            jsonSample.add([
                (g.message(code:C.DEFAULT_NAME_LABEL_CODE)) : sample.name,
                (g.message(code:C.DEFAULT_DESCRIPTION_LABEL_CODE)) : sample.description,
                (g.message(code:C.DEFAULT_FILETYPE_LABEL_CODE)) : sample.fileContentType,   
                (g.message(code:C.DEFAULT_FILENAME_LABEL_CODE)) : sample.originalFilename,
                (g.message(code:C.DEFAULT_MD5_LABEL)) : sample.md5,
                (g.message(code:C.DEFAULT_SHA1_LABEL)) : sample.sha1,
                (g.message(code:C.DEFAULT_SHA256_LABEL)) : sample.sha256,
                (g.message(code:C.DEFAULT_SHA512_LABEL)) : sample.sha512,
                (g.message(code:C.JS_DATA_TABLE_ACTION_LABEL))
                    : generateActionButtons(sample.id, sample.name)])
        }
        
        aaData.put('aaData', jsonSample)
                        
        return render(aaData as JSON)
    }
    
    /**
     * Returns an initialization mapping from an sample data table as an JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "Sample", action: "dataTable"),
                "aoColumns": [
                    ["mData": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_NAME_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_FILETYPE_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_FILENAME_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_DESCRIPTION_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_MD5_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_SHA1_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_SHA256_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_SHA512_LABEL)]
                    
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL), "bSortable": false, "aTargets": [0]],
                    ["sTitle": g.message(code: C.DEFAULT_NAME_LABEL_CODE), "aTargets": [1]],
                    ["sTitle": g.message(code: C.DEFAULT_FILETYPE_LABEL_CODE), "aTargets": [2]],
                    ["sTitle": g.message(code: C.DEFAULT_FILENAME_LABEL_CODE), "aTargets": [3]],
                    ["sTitle": g.message(code: C.DEFAULT_DESCRIPTION_LABEL_CODE), "aTargets": [4]],
                    ["sTitle": g.message(code: C.DEFAULT_MD5_LABEL), "aTargets": [5], "bVisible": false],
                    ["sTitle": g.message(code: C.DEFAULT_SHA1_LABEL), "aTargets": [6], "bVisible": false],
                    ["sTitle": g.message(code: C.DEFAULT_SHA256_LABEL), "aTargets": [7], "bVisible": false],
                    ["sTitle": g.message(code: C.DEFAULT_SHA512_LABEL), "aTargets": [8], "bVisible": false]
                    
                ]
            ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
    
    /**
     * Generates an html5 code for specific action buttons in an data table.
     * 
     * @param id Identity from sample object
     * @param name Sample name
     * 
     * @return generated html code
     **/
    private String generateActionButtons(Long id, String name) {
        Map params = [
            id : id,
            url: [
                modal : g.createLink(url: [controller : 'Sample', action : 'deleteModal', params:[id:id]]),
                delete : g.createLink(url: [controller : 'Sample', action : 'delete', params:[id:id]])
            ]
        ]
            
        String deleteButton = g.htmlTag(tag : 'a', attributes : [
                "href=''",
                "onClick='\$(this).showDeleteDialog(" + (params as JSON) + ");return false;'"
            ], containsEndTag : true ,innerHtml : 'Löschen')

        String editButton = g.htmlTag(tag : 'a', attributes : [
                "href=\"\"",
                "onClick=\"openForm(true ," + id  + ");return false;\""
            ], containsEndTag : true ,innerHtml : 'Bearbeiten')     
        
        return editButton + "<br>" + deleteButton
    }
}