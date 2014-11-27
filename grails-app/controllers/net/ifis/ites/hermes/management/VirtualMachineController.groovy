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

import grails.converters.JSON
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

/**
 * Controller to handle all incoming requests for an Virtual machine.
 * 
 * @author Andreas Sekulski
 **/
class VirtualMachineController {
    
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
     * Renders an create formular from an vm object.
     **/    
    def create() {
        return render(view : "VM", 
            model:[
                instance : new VirtualMachine(),
                formTitel : message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [message(code: C.VM_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE),
                dataUploadRequired : true,
                url : createLink(controller:'VirtualMachine', action: 'save')])
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
     * Request parameters and convert it to his VM params and try to
     * save it.
     **/
    def save() {
        StatusMessage responseJSON
        Map convertParams
        
        // Check if an given id from hypervisor or operating system exists   
        if(params.os?.id) {
            try {
                administrationService.getEntity(OperatingSystem, new Long(params.os.id))
            } catch(IllegalArgumentException iae) {
                responseJSON = new StatusMessage()

                responseJSON.setMessage(
                    g.message(
                        code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                        args: [message(code: C.OPERATING_SYSTEM_LABEL_CODE), params.os.id]))

                responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

                return render(responseJSON.getJSON())
            }
        }

        if(params.hypervisor?.id) {            
            try {
                administrationService.getEntity(Hypervisor, new Long(params.hypervisor?.id))
            } catch(IllegalArgumentException iae) {
                responseJSON = new StatusMessage()

                responseJSON.setMessage(
                    g.message(
                        code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                        args: [message(code: C.HYPERVISOR_LABEL_CODE), params.hypervisor.id]))

                responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

                return render(responseJSON.getJSON())
            }
        }
        
        convertParams = factoryParamsService.convertParams(VirtualMachine, params)
        
        if(!convertParams.file) {
            responseJSON = new StatusMessage()
            responseJSON.setMessage(g.message(code: C.DEFAULT_FILE_EXCEPTION))
            responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
            return render(responseJSON.getJSON())
        }
               
        responseJSON = administrationService.create(new VirtualMachine() , convertParams)
        return render(responseJSON.getJSON())
    }
    
    /**
     * Generates an edit view from an vm if an vm object exists
     *
     * @params id Identity number from an vm
     **/
    def edit(Long id) {
        StatusMessage responseJSON 
        VirtualMachine vm
        
        try {
            vm = administrationService.getEntity(VirtualMachine, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.VM_LABEL_CODE), id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(responseJSON.getJSON())
        }

        return render(view : "VM", 
            model : [
                instance : vm,
                formTitel : message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [message(code: C.VM_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE),
                dataUploadRequired : false,
                url : createLink(controller:'VirtualMachine', action: 'update')
            ])      
    }
    
    /**
     * Request parameters and convert it to his VM params and try to
     * update an vm object.
     **/
    def update() {
        StatusMessage responseJSON
        Map convertParams
        VirtualMachine vm
        
        // Check if an given id from hypervisor or operating system exists
        if(params.os?.id) {
            try {
                administrationService.getEntity(OperatingSystem, new Long(params.os.id))
            } catch(IllegalArgumentException iae) {
                responseJSON = new StatusMessage()

                responseJSON.setMessage(
                    g.message(
                        code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                        args: [message(code: C.OPERATING_SYSTEM_LABEL_CODE), new Long(params.os.id)]))

                responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

                return render(responseJSON.getJSON())
            }
        }

        if(params.hypervisor?.id) {            
            try {
                administrationService.getEntity(Hypervisor, new Long(params.hypervisor.id))
            } catch(IllegalArgumentException iae) {
                responseJSON = new StatusMessage()

                responseJSON.setMessage(
                    g.message(
                        code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                        args: [message(code: C.HYPERVISOR_LABEL_CODE), new Long(params.hypervisor.id)]))

                responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

                return render(responseJSON.getJSON())
            }
        }
        
        convertParams = factoryParamsService.convertParams(VirtualMachine, params)
          
        try {
            vm = administrationService.getEntity(VirtualMachine, convertParams.id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.VM_LABEL_CODE), convertParams.id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(responseJSON.getJSON())
        }
                        
        // Checks if an user has update this hypervisor before
        responseJSON = administrationService.checkVersion(vm, convertParams.version)
        if(responseJSON) {
            return render(responseJSON.getJSON())
        }
        
        responseJSON = administrationService.update(vm, convertParams)
     
        return render(responseJSON.getJSON())
    }
    
    /**
     * Try to delete an existing virtual machine
     * 
     * @params id Identity number from virtual machine to delete
     **/
    def delete(Long id) {
        StatusMessage responseJSON
        VirtualMachine vm
        
        try {
            vm = administrationService.getEntity(VirtualMachine, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.VM_LABEL_CODE), id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
        
        if(Job.findAllByVirtualMachine(vm).size() > 0) {
            responseJSON = new StatusMessage()
            responseJSON.setStatusCode(SEC.DATA_INTEGRATION_CODE)
            responseJSON.setMessage(g.message(
                    code: C.DEFAULT_DATA_INTEGRATION_ERROR_CODE, 
                    args: [g.message(code : C.VM_LABEL_CODE), vm]))    
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
        
        responseJSON = administrationService.delete(vm)
                
        return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
    }
    
    /**
     * Shows an modal window for an deletion for an vm.
     * 
     * @params id Identity number from an vm
     **/
    def deleteModal(Long id) {
        StatusMessage responseJSON
        VirtualMachine vm
        
        try {
            vm = administrationService.getEntity(VirtualMachine, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.VM_LABEL_CODE), id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }

        return render(template : "../administration/modal", 
            model : [
                message : g.message(code: C.DEFAULT_BUTTON_DELETE_MESSAGE, args: [g.message(code: C.VM_LABEL_CODE), vm.name])
            ])   
    }
    
    /**
     * Generates all vm data as an json data-table data.
     **/
    def dataTable() {
        HashMap aaData = new HashMap()
        List jsonVM = new ArrayList()

        for (vm in VirtualMachine.list()) {
            jsonVM.add([
                    (g.message(code:C.DEFAULT_NAME_LABEL_CODE)) : vm.name,
                    (g.message(code:C.DEFAULT_DESCRIPTION_LABEL_CODE)) : vm.description,
                    (g.message(code:C.DEFAULT_FILENAME_LABEL_CODE)) : vm.originalFilename,   
                    (g.message(code:C.HYPERVISOR_LABEL_CODE)) : vm.hypervisor.toString(),
                    (g.message(code:C.OPERATING_SYSTEM_LABEL_CODE)) : vm.os.toString(),
                    (g.message(code:C.JS_DATA_TABLE_ACTION_LABEL)) : 
                    generateActionButtons(vm.id, vm.name)])
        }
        
        aaData.put('aaData', jsonVM)
                        
        return render(aaData as JSON)
    }
    
    /**
     * Returns an initialization mapping from an vm data table as an JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "VirtualMachine", action: "dataTable"),
                "aoColumns": [
                    ["mData": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_NAME_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_FILENAME_LABEL_CODE)],
                    ["mData": g.message(code: C.HYPERVISOR_LABEL_CODE)],
                    ["mData": g.message(code: C.OPERATING_SYSTEM_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_DESCRIPTION_LABEL_CODE)]
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL), "bSortable": false, "aTargets": [0]],
                    ["sTitle": g.message(code: C.DEFAULT_NAME_LABEL_CODE), "aTargets": [1]],
                    ["sTitle": g.message(code: C.DEFAULT_FILENAME_LABEL_CODE), "aTargets": [2]],
                    ["sTitle": g.message(code: C.HYPERVISOR_LABEL_CODE), "aTargets": [3]],
                    ["sTitle": g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), "aTargets": [4]],
                    ["sTitle": g.message(code: C.DEFAULT_DESCRIPTION_LABEL_CODE), "aTargets": [5]]
                ]
            ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
    
    /**
     * Generates an html5 code for specific action buttons in an data table.
     * 
     * @param id Identity from vm object
     * @param name vm name
     * 
     * @return generated html code
     **/
    private String generateActionButtons(Long id, String name) {
        
        Map params = [
            id : id,
            url: [
                modal : g.createLink(url: [controller : 'VirtualMachine', action : 'deleteModal', params:[id:id]]),
                delete : g.createLink(url: [controller : 'VirtualMachine', action : 'delete', params:[id:id]])
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