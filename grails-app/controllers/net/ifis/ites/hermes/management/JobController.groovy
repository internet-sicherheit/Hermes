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
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

/**
 * Controller to handle all incoming requests for an job.
 * 
 * @author Andreas Sekulski
 **/
class JobController {
    
    /**
     * Defined static priorities from job priority
     **/
    private final Map PRIORITY = [0: (C.OPTION_VALUE_LOW_CODE),
        1: (C.OPTION_VALUE_MEDIUM_CODE),
        2: (C.OPTION_VALUE_HIGH_CODE)]

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

    def springSecurityService
    
    /**
     * Injected page locator from groovy to check if an template or view exists
     **/
    def GrailsConventionGroovyPageLocator groovyPageLocator
    
    /**
     * Renders an create formular from an job object.
     **/   
    def create() {
        return render(view : "create", 
            model:[
                mapGroup: administrationService.getVMSFromHypervisor(),
                formTitel : message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [message(code: C.JOB_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE),
                isCreateForm : true,
                url : createLink(controller:'Job', action: 'save')])
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
     * Request parameters and convert it to his Job params and try to
     * save it.
     **/
    def save() {
        StatusMessage responseJSON
        Map convertParams = factoryParamsService.convertParams(Job, params)
        
        println(convertParams)
        
        convertParams.sensors = administrationService.getListData(Sensor, convertParams.sensors)
        convertParams.samples = administrationService.getListData(Sample, convertParams.samples)
        convertParams.vms = administrationService.getListData(VirtualMachine, convertParams.vms)
        
        if(administrationService.containsErrors(convertParams.sensors) 
            || administrationService.containsErrors(convertParams.samples)
            || administrationService.containsErrors(convertParams.vms)) {
            responseJSON = new StatusMessage()
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            responseJSON.setMessage(administrationService.getErrors(convertParams.sensors) 
                + administrationService.getErrors(convertParams.samples) 
                + administrationService.getErrors(convertParams.vms))  
            return render(responseJSON.getJSON())
        }
        
        responseJSON = administrationService.createJobs(convertParams)  
        
        return render(responseJSON.getJSON())
    }
    
    /**
     * Try to delete an existing job
     * 
     * @params id Identity number from an job
     **/
    def delete(Long id) {
        StatusMessage responseJSON
        Job job
        
        try {
            job = administrationService.getEntity(Job, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.JOB_LABEL_CODE), id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }
                
        responseJSON = administrationService.delete(job)
                
        return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
    }   
    
    /**
     * Shows an modal window for an deletion for an Job.
     * 
     * @params id Identity number from an job
     **/
    def deleteModal(Long id) {
        StatusMessage responseJSON
        Job job
        
        try {
            job = administrationService.getEntity(Job, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()

            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.JOB_LABEL_CODE), id]))

            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)

            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
        }

        return render(template : "../administration/modal", 
            model : [
                message : g.message(code: C.DEFAULT_BUTTON_DELETE_MESSAGE, args: [g.message(code: C.JOB_LABEL_CODE), job.name])
            ]) 
    }
    
    /**
     * Generates all job data as an json data-table data.
     **/
    def dataTable() {
        String startTime
        String sensorName 
        HashMap aaData = new HashMap()
        List jsonJob = new ArrayList()
        User currentUser = springSecurityService.getCurrentUser()
        
        for (job in Job.findAllPublicAndOwnerJobs(currentUser)) {
            
            startTime = ""
            
            if(!job.startTime) {
                startTime = g.formatDate(format: g.message(code: C.DATE_LABEL_CODE), date: job.startTime)
            }
            
            sensorName = job.sensor.toString()
            if(!job.sensor) {
                sensorName = g.message(code: C.DEFAULT_CUCKOO_SENSOR)
            }
            
            jsonJob.add([
                    (g.message(code:C.DEFAULT_NAME_LABEL_CODE)) : job.name,
                    (g.message(code:C.SENSOR_LABEL_CODE)) : sensorName,
                    (g.message(code:C.SAMPLE_LABEL_CODE)) : job.sample.toString(),   
                    (g.message(code:C.VM_LABEL_CODE)) : job.virtualMachine.toString(),   
                    (g.message(code:C.JOB_MEMORYDUMP_LABEL_CODE)) : g.createBooleanData(value : job.memoryDump),  
                    (g.message(code:C.JOB_PRIORITY_LABEL_CODE)) : g.message(code : PRIORITY.get(job.priority)),
                    (g.message(code:C.DEFAULT_EARLY_START_DATE)) : startTime,
                    (g.message(code:C.JOB_TIMEOUT_LABEL_CODE)) : g.formatDate(
                        format: g.message(code: C.DEFAULT_TIME_FORMAT_CODE), 
                        date: job.timeout),
                    (g.message(code:C.DATE_LABEL_CODE)) : g.formatDate(
                        format : g.message(code: C.DEFAULT_DATE_TIME_FORMAT_CODE), 
                        date : job.simulatedTime),
                    (g.message(code: C.DEFAULT_JOB_REBOOT_LABEL)) : g.formatDate(
                        format: g.message(code: C.DEFAULT_TIME_FORMAT_CODE), 
                        date: job.reboot), 
                    (g.message(code:C.DEFAULT_EARLY_START_DATE)) : g.formatDate(
                        format : g.message(code: C.DEFAULT_DATE_TIME_FORMAT_CODE), 
                        date : job.startTime),
                    (g.message(code:C.JS_DATA_TABLE_ACTION_LABEL)) : 
                    generateActionButtons(job.id, job.name)])
        }
           
        aaData.put('aaData', jsonJob)
                        
        return render(aaData as JSON)
    }
    
    /**
     * Returns an initialization mapping from an job data table as an JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "Job", action: "dataTable"),
                "aoColumns": [
                    ["mData": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_NAME_LABEL_CODE)],
                    ["mData": g.message(code: C.SENSOR_LABEL_CODE)],
                    ["mData": g.message(code: C.SAMPLE_LABEL_CODE)],
                    ["mData": g.message(code: C.VM_LABEL_CODE)],
                    ["mData": g.message(code: C.JOB_MEMORYDUMP_LABEL_CODE)],
                    ["mData": g.message(code: C.JOB_PRIORITY_LABEL_CODE)],
                    ["mData": g.message(code: C.JOB_TIMEOUT_LABEL_CODE)],
                    ["mData": g.message(code: C.DATE_LABEL_CODE)],
                    ["mData": g.message(code: C.DEFAULT_JOB_REBOOT_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_EARLY_START_DATE)]
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL), "bSortable": false, "aTargets": [0]],
                    ["sTitle": g.message(code: C.DEFAULT_NAME_LABEL_CODE), "aTargets": [1]],
                    ["sTitle": g.message(code: C.SENSOR_LABEL_CODE), "aTargets": [2]],
                    ["sTitle": g.message(code: C.SAMPLE_LABEL_CODE), "aTargets": [3]],
                    ["sTitle": g.message(code: C.VM_LABEL_CODE), "aTargets": [4]],
                    ["sTitle": g.message(code: C.JOB_MEMORYDUMP_LABEL_CODE), "aTargets": [5]],
                    ["sTitle": g.message(code: C.JOB_PRIORITY_LABEL_CODE), "aTargets": [6]],
                    ["sTitle": g.message(code: C.JOB_TIMEOUT_LABEL_CODE), "aTargets": [7]],
                    ["sTitle": g.message(code: C.DATE_LABEL_CODE), "aTargets": [8]],
                    ["sTitle": g.message(code: C.DEFAULT_JOB_REBOOT_LABEL), "aTargets": [9]],
                    ["sTitle": g.message(code: C.DEFAULT_EARLY_START_DATE), "aTargets": [10]]
                ]
            ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
    
    /**
     * Generates an html5 code for specific action buttons in an data table.
     * 
     * @param id Identity from job object
     * @param name job name
     * 
     * @return generated html code
     **/
    private String generateActionButtons(Long id, String name) {
        Map params = [
            id : id,
            url: [
                modal : g.createLink(url: [controller : 'Job', action : 'deleteModal', params:[id:id]]),
                delete : g.createLink(url: [controller : 'Job', action : 'delete', params:[id:id]])
            ]
        ]
            
        String deleteButton = g.htmlTag(tag : 'a', attributes : [
                "href=''",
                "onClick='\$(this).showDeleteDialog(" + (params as JSON) + ");return false;'"
            ], containsEndTag : true ,innerHtml : 'Löschen')
        
        return deleteButton
    }
}
