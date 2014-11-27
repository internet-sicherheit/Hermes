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
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

import grails.converters.JSON

/**
 * Controller to handle all incoming requests for an Hypervisor.
 * 
 * @author Andreas Sekulski
 **/
class HypervisorController {
    
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
     * Injected page locator from groovy to check if an template or view exists
     **/
    def GrailsConventionGroovyPageLocator groovyPageLocator
    
    /**
     * Renders an create formular from an hypervisor object.
     **/
    def create() {
        return render(view : "Hypervisor", 
            model:[
                instance : new Hypervisor(),
                formTitel : message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [message(code: C.HYPERVISOR_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE),
                url : createLink(controller:'Hypervisor', action: 'save')])
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
     * Request parameters and convert it to his Hypervisor params and try to
     * save it.
     **/
    def save() {
        StatusMessage responseJSON 
        Hypervisor hypervisor = new Hypervisor() 
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(Hypervisor, params)
        
        // Save hypervisor
        responseJSON = administrationService.create(hypervisor, convertParams)
        
        return render(responseJSON.getJSON())
    }
                
    /**
     * Generates an edit view from an hypervisor if an hypervisor exists
     *
     * @params id Identity number from hypervisor
     **/
    def edit(Long id) {
        StatusMessage responseJSON 
        Hypervisor hypervisor
        
        try {
            hypervisor = administrationService.getEntity(Hypervisor, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.HYPERVISOR_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
        
        return render(view : "Hypervisor", 
            model : [
                instance : hypervisor,
                formTitel : message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [message(code: C.HYPERVISOR_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE),
                url : createLink(controller:'Hypervisor', action: 'update')
            ])      
    }
    
    /**
     * Request parameters and convert it to his Hypervisor params and try to
     * update an hypervisor.
     **/
    def update() {
        Hypervisor hypervisor
        StatusMessage responseJSON
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(Hypervisor, params)
        
        try {
            hypervisor = administrationService.getEntity(Hypervisor, convertParams.id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.HYPERVISOR_LABEL_CODE), convertParams.id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
        
        // Checks if an user has update this hypervisor before
        responseJSON = administrationService.checkVersion(hypervisor, convertParams.version)
        if(responseJSON) {
            return render(responseJSON.getJSON())
        }
        
        // Update hypervisor
        responseJSON = administrationService.update(hypervisor, convertParams)

        return render(responseJSON.getJSON())
    }
    
    /**
     * Shows an modal window for an deletion for an Hypervisor.
     * 
     * @params id Identity number from an hypervisor
     **/
    def deleteModal(Long id) {
        StatusMessage responseJSON
        Hypervisor hypervisor
        
        try {
            hypervisor = administrationService.getEntity(Hypervisor, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.HYPERVISOR_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }

        return render(template : "../administration/modal", 
            model : [
                message : g.message(code: C.DEFAULT_BUTTON_DELETE_MESSAGE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), hypervisor.name])
            ])   
    }
    
    /**
     * Try to delete an existing hypervisor
     * 
     * @params id Identity number from hypervisor to delete
     **/
    def delete(Long id) {
        StatusMessage responseJSON
        Hypervisor hypervisor
        
        try {
            hypervisor = administrationService.getEntity(Hypervisor, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.HYPERVISOR_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
        
        responseJSON = administrationService.delete(hypervisor)
        return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
    }
    
    /**
     * Generates all hypervisors as an json data-table data.
     **/
    def dataTable() {
        HashMap aaData = new HashMap()
        List jsonHypervisor = new ArrayList()

        for (hypervisor in Hypervisor.list()) {
            jsonHypervisor.add([
                (g.message(code:C.DEFAULT_NAME_LABEL_CODE)) : hypervisor.name,
                (g.message(code:C.JS_DATA_TABLE_ACTION_LABEL)) : 
                    generateActionButtons(hypervisor.id, hypervisor.name)])
        }
        
        aaData.put('aaData', jsonHypervisor)
                        
        return render(aaData as JSON)
    }
    
    /**
     * Returns an initialization mapping from an hypervisor data table as an
     * JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "Hypervisor", action: "dataTable"),
                "aoColumns": [
                    ["mData": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_NAME_LABEL_CODE)]
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL), "bSortable": false, "aTargets": [0]],
                    ["sTitle": g.message(code: C.DEFAULT_NAME_LABEL_CODE), "aTargets": [1]]
                ]
            ]
            
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
    
    /**
     * Generates an html5 code for specific action buttons in an data table.
     * 
     * @param id Identity from hypervisor object
     * @param name Hypervisor name
     * 
     * @return generated html code
     **/
    private String generateActionButtons(Long id, String name) {
        
        Map params = [
            id : id,
            url: [
                modal : g.createLink(url: [controller : 'Hypervisor', action : 'deleteModal', params:[id:id]]),
                delete : g.createLink(url: [controller : 'Hypervisor', action : 'delete', params:[id:id]])
            ]
        ]
        
        String deleteButton = g.htmlTag(tag : 'a', attributes : [
                "href=''",
                "onClick='\$(this).showDeleteDialog("+ (params as JSON) +");return false;'"
            ], containsEndTag : true ,innerHtml : 'Löschen')

        String editButton = g.htmlTag(tag : 'a', attributes : [
                "href=\"\"",
                "onClick=\"openForm(true ," + id  + ");return false;\""
            ], containsEndTag : true ,innerHtml : 'Bearbeiten')     
        
        return editButton + "<br>" + deleteButton
    }
}