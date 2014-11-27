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

package net.ifis.ites.hermes.publishing

import grails.converters.JSON
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.StatusMessage
import net.ifis.ites.hermes.management.*

/**
 * Controller to handle all incoming requests for this Jobstatus view.
 * 
 * @author Andreas Sekulski
 **/
class JobstatusController {
    
    /**
     * Injected JobstatusService
     */
    def jobstatusService
    
    /**
     * Injected data table service
     **/
    def dataTableService

    /**
     * Renders the jobstatus view by default.
     */   
    def index() {
        return redirect(action : 'jobstatus')
    }
        
    /**
     * Renders an dynatree
     * 
     * @params openNodes[] the nodes they are open
     * @params selectedNodes[] the nodes they are checked
     * 
     * @return an dynatree as json
     */   
    def renderDynatree() {  
        Object openNodes = params."openNodes[]"
        Object selectedNodes = params."selectedNodes[]"       
        return render(jobstatusService.buildTree(openNodes, selectedNodes).getJSON())
    }
    
    /**
     * Renders for an node the child element.
     * 
     * @params id the id from the node
     * @params selected the nodes if is checked (true) or not (false)
     * 
     * @return an child as json
     */   
    def renderChildren(Long id, Boolean selected) {
        return render(jobstatusService.getChildren(id, selected) as JSON)
    }
           
    /**
     * Renders filters job from the given selected node in the dynatree.
     * 
     * @params nodeselection the selected node.
     * @params showHiddenJobs show hidden jobs
     * 
     * @return an list of jobs that should be rendered to an joblist
     */
    def renderFilterJobs(String nodeSelection) {
        Map aaData = new HashMap()  
        aaData.put('aaData', jobstatusService.getJobsByFilter(jobstatusService.convertTree(nodeSelection)))                              
        return render(aaData as JSON)
    }
           
    /**
     * Renders the jobstatus view.
     **/
    def jobstatus() {
        return render(view : 'jobstatus')
    }
    
    /**
     * Renders all published jobs for data tables for an specific status type.
     * 
     * @param type - Status typer from published job
     * 
     * @return an json file from all published jobs data
     **/
    def allJobs(String type) {
        List tableData
        HashMap aaData = new HashMap()
        aaData.put('aaData', jobstatusService.getJobsByType(type))
        return render(aaData as JSON)
    }

    /**
     * Detail view from an logfile if exists in Job.
     * 
     * @param id - Identifier from an published job.
     * 
     * @return renders the detail view from an Job if exists else 
     * renders an not found error message.
     **/
    def detail(Long id) {
        Job pj = Job.get(id)
        
        // If published job not exists render an error message
        if (!pj) {
            return render(text : message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.JOB_LABEL_CODE), id]))
        }
        
        if(!pj.errorLog) {
            return render(text : '')
        }
        
        return render(text : pj.errorLog)
    }
    
    /**
     * Detail view from an job if exists.
     * 
     * @param id - Identifier from an job.
     * 
     * @return renders the detail view from an job if exists else 
     * renders an not found error message.
     **/
    def jobdetail(Long id) {
        Job job = Job.get(id)
        
        if(!job) {
            return render(text : message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.JOB_LABEL_CODE), id]))
        }
        
        return render(template:"jobdetail" , model:[job : job])
    }
    
    /**
     * Returns an initialization mapping from an jobstatus data table as an JSON.
     **/
    def initDataTable() {
        
        Map params = [
                "aoColumns": [
                    ["mData": g.message(code: C.DEFAULT_STATE_LABEL)],
                    ["mData": g.message(code: C.JS_START_DATE)],
                    ["mData": g.message(code: C.DEFAULT_IP_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_IP_LABEL)],
                    ["mData": g.message(code: C.HYPERVISOR_LABEL_CODE)],
                    ["mData": g.message(code: C.VM_LABEL_CODE)],
                    ["mData": g.message(code: C.SAMPLE_LABEL_CODE)],
                    ["mData": g.message(code: C.SENSOR_LABEL_CODE)],
                    ["mData": g.message(code: C.JS_DATA_TABLE_LOGFILE_LABEL)]
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.DEFAULT_STATE_LABEL), "aTargets": [0]],
                    ["sTitle": g.message(code: C.JS_START_DATE), "aTargets": [1]],
                    ["sTitle": g.message(code: C.NODE_LABEL_CODE), "aTargets": [2]],
                    ["sTitle": g.message(code: C.DEFAULT_IP_LABEL), "aTargets": [3]],
                    ["sTitle": g.message(code: C.HYPERVISOR_LABEL_CODE), "aTargets": [4]],
                    ["sTitle": g.message(code: C.VM_LABEL_CODE), "aTargets": [5]],
                    ["sTitle": g.message(code: C.SAMPLE_LABEL_CODE), "aTargets": [6]],
                    ["sTitle": g.message(code: C.SENSOR_LABEL_CODE), "aTargets": [7]],
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_LOGFILE_LABEL), "bSortable": false, "aTargets": [8]]
                ],
                "aaSorting": [[1, "desc"]]
            ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
}