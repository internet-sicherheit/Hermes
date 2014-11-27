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

import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.Dynanode
import net.ifis.ites.hermes.util.Dynatree
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import net.ifis.ites.hermes.management.*

/**
 * Service to generate jobstatus data.
 *
 * @author Andreas Sekulski
 */
class JobstatusService {
    
    /**
     * Injected TagLib Service
     **/
    def g = new ApplicationTagLib()

    def springSecurityService
    
    /**
     * Constant for an tree splitter for the id from an node
     **/
    private static final String TREE_SPLITTER = ","
    
    /**
     * Constant for an tree splitter for the id from an sample sensor
     **/
    private static final String ELEMENT_SPLITTER = "."
    
    /**
     * Constant from the key from an sampleID from an node
     **/
    private static final String KEY_SAMPLE = "sampleID"
    
    /**
     * Constant from the key from an sensorID from an node
     **/
    private static final String KEY_SENSOR = "sensorID"
    
    /**
     * Constant array position from an sample
     **/
    private static final int ARRAY_SAMPLE = 0
    
    /**
     * Constant array position from an sensor
     **/
    private static final int ARRAY_SENSOR = 1

    /**
     * Type constants for jobs as an mapping
     **/
    private static final Map states = [
        'Published' : Job.JobState.PUBLISHED,
        'Submitted' : Job.JobState.SUBMITTED,
        'Processing' : Job.JobState.PROCESSING,
        'Success' : Job.JobState.SUCCESS,
        'Failure' : Job.JobState.FAILURE,
        'Pending' : Job.JobState.PENDING
    ]
    
    /**
     * Models Jobs from Jobs for the jobstatus data table with an
     * given state.
     * 
     * @params type Jobs type to find
     * 
     * @return an an empty published job list or all published jobs
     **/
    public List getJobsByType(String type) {
        
        List<Job> jobs
        User currentUser = springSecurityService.getCurrentUser()
                
        if(type.equals('All')) {
            jobs = Job.findAllPublicAndOwnerJobs(currentUser)
        } else if(type.equals('Active')) {
            jobs = Job.withCriteria {
                or { 
                    eq 'state', Job.JobState.PUBLISHED
                    eq 'state', Job.JobState.SUBMITTED
                    eq 'state', Job.JobState.PROCESSING
                    eq 'state', Job.JobState.PENDING
                }
                or {
                    eq 'owner', currentUser
                    isNull 'owner'
                }
            }
        } else {
            Job.JobState filterState = states.get(type)
            jobs = Job.findAllForState(filterState, currentUser)
        }
        
        return generateDataTable(jobs)
    }

    /**
     * Build an Dynatree from the given openNodes and selectedNodes.
     * 
     * @params openNodes an String with an single selected open node or an 
     * Array of String that are open.
     * @params selecedNodes an String with an single checked node or an Array
     * of selectedNodes that are checked.
     * 
     * @return an created Dynatree
     **/
    public Dynatree buildTree(Object openNodes, Object selectedNodes) {
        Dynanode node
        
        Boolean isLazy = false
        Dynatree tree = new Dynatree()
                
        Map isSelected = getSelectedNodes(selectedNodes)
        Map openChildren = getOpenNodes(openNodes, isSelected)      
        
        
        for (sample in Sample.list()) {           
            if(openChildren.get(sample.id + "") == null) {
                isLazy = true 
            } else {
                isLazy = false 
            }
            
            node = new Dynanode(
                sample.id + "", sample.name , 
                isSelected.get(sample.id + ""), 
                true, 
                isLazy, 
                openChildren.get(sample.id + ""))
            
            tree.addNode(node) 
        }
        
        return tree
    }
    
    /**
     * Gets an children from an Dynatree.
     * 
     * @params id An Sample id
     * @params selected Is Node selected (true) or not (false)
     * 
     * @return An List with all childrens (Sensors) from sample.
     * 
     **/
    public List getChildren(Long id, Boolean selected) {
        def results
        
        Sample sample = Sample.get(id)
        
        ArrayList treeJson = new ArrayList()
        ArrayList sensors = new ArrayList()
        
        if(!sample) {
            return treeJson
        }
        
        results = Job.findAllBySample(sample)
        
        for (job in results) {
            String childrenID = sample.id + TREE_SPLITTER + job.sensor?.id
            
            String sensorName = job.sensor?.name
            if(!sensorName) {
                sensorName = g.message(code: C.DEFAULT_CUCKOO_SENSOR)
            }
            
            Dynanode children = new Dynanode(childrenID, sensorName, selected, null, null, null)
            
            if(!sensors.contains(job.sensor)) {
                treeJson.add(children.getParams())
                sensors.add(job.sensor)
            }
        }
        
        return treeJson
    }
    
    /**
     * Gets an list of jobs who should be displayed in the view.
     * 
     * @params nodeSelection current selected node from an dynatree.
     * 
     * @return an list of all new jobs.
     **/
    public List getJobsByFilter(Map nodeSelection) {
        
        Sample sample
        List sensors     
        List jobList = new ArrayList()
        User currentUser = springSecurityService.getCurrentUser()
        
        for(node in nodeSelection) {
            sample = Sample.get(node.key)
            sensors = node.value

            jobList.addAll(generateDataTable(Job.findAllBySampleAndSensorsForOwner(sample, sensors, currentUser)))
        }
        
        return jobList
    }
        
    /**
     * Generates the published job data table data.
     * 
     * @params Jobs an list selection from Jobs
     * 
     * @return an generated JSON list for data table configuration.
     **/
    private List generateDataTable(List<Job> jobs) {
        
        List pJList = new ArrayList() 
        String imageFilename
        
        for (pj in jobs) {
            
            if(pj.state.toString().equals('PENDING')) {
                imageFilename = "goomba.gif"
            } else {
                imageFilename = pj.state.toString() + ".png" 
            }
            
            pJList.add([
                    (g.message(code: C.JS_START_DATE)) : "<div class='text-center'>" 
                    + g.formatDate(
                        format : g.message(code: C.DEFAULT_DATE_TIME_FORMAT_CODE),
                        date : pj.publishingDate) + "</div>",
                    (g.message(code: C.DEFAULT_STATE_LABEL)) : "<div>" 
                    + g.img(dir: 'assets/icons/state/', file: imageFilename , width: "16px") 
                    + ' '
                    + pj.state.toString() + "</div>",
                    (g.message(code: C.NODE_LABEL_CODE)) : pj.node?.name,
                    (g.message(code: C.DEFAULT_IP_LABEL)) : pj.node?.networkAddress,
                    (g.message(code: C.HYPERVISOR_LABEL_CODE)) : pj.virtualMachine.hypervisor.name,
                    (g.message(code: C.VM_LABEL_CODE)) : pj.virtualMachine.name,
                    (g.message(code: C.SAMPLE_LABEL_CODE)) : pj.sample.name,
                    (g.message(code: C.SENSOR_LABEL_CODE)) : pj.sensor?.name,
                    (g.message(code: C.JS_DATA_TABLE_LOGFILE_LABEL)) : getLogFile(pj)
                ])
        }
        
        return pJList
    }
    
    /**
     * Converts the selection of all dynatree nodes in an hash map.
     * 
     * Like "18.19", "18.20" --> Key 18 Value 19,20
     * "18" --> Key 18 Value Empty List
     * 
     * @params selectedNodes list of all selected nodes from an dynatree.
     * 
     * @return an hash map value with all selected nodes if params null an empty
     * list will be returned.
     **/
    public Map convertTree(String selectedNodes) {   
        Map idMap = new HashMap()
        List list 
        String[] ids  // Selected Identifier to Filter
        String[] sampleSensor // Contains Sample & Sensor ID
        
        if(!selectedNodes) {
            return idMap
        }
        
        if(selectedNodes.contains(TREE_SPLITTER)) {
            ids = selectedNodes.split("\\" + TREE_SPLITTER)
            for(id in ids) {
                if(id.contains(ELEMENT_SPLITTER)) {
                    sampleSensor = id.split("\\" + ELEMENT_SPLITTER)
                    if(idMap.containsKey(sampleSensor[ARRAY_SAMPLE])) {
                        idMap.get(sampleSensor[ARRAY_SAMPLE]).add(sampleSensor[ARRAY_SENSOR])
                    } else {
                        list = new ArrayList()
                        list.add(sampleSensor[ARRAY_SENSOR])
                        idMap.put(sampleSensor[ARRAY_SAMPLE], list)
                    } 
                } else {
                    if(!idMap.containsKey(id)) {
                        idMap.put(id, new ArrayList())
                    }
                }
            }
        } else if(!selectedNodes.isEmpty()) {
            if(!idMap.containsKey(selectedNodes) && !selectedNodes.contains(ELEMENT_SPLITTER)) {
                idMap.put(selectedNodes, new ArrayList())
            } else {
                sampleSensor = selectedNodes.split("\\" + ELEMENT_SPLITTER)
                list = new ArrayList()
                list.add(sampleSensor[ARRAY_SENSOR])
                idMap.put(sampleSensor[ARRAY_SAMPLE], list)
            }
        }
        
        return idMap
    }
    
    /**
     * Get an Map with all Open nodes from an Dynatree,
     * 
     * @params openNodes an String with an single selected open node or an 
     * Array of String that are open.
     * @params selecedNodes an String with an single checked node or an Array
     * of selectedNodes that are checked.
     * 
     * @return an map with all open nodes. Key value is the id from the node
     * and value the children element of it.
     **/
    private Map getOpenNodes(Object openNodes, Map isSelected) {
        Map openChildren = new HashMap()
        
        if(openNodes) {
            
            def results
            Sample sample
            String[] sampleSensor // Contains Sample & Sensor ID
            
            ArrayList sensors = new ArrayList()
            ArrayList childrens = new ArrayList()
            
            if(openNodes instanceof String) {
                // Only one sample is open
                sample = Sample.get(openNodes)
                results = Job.findAllBySample(sample)
                
                for (job in results) {
                    if(!sensors.contains(job.sensor)) {
                        Sensor sensor = job.sensor
                        String childrenID = sample.id + ELEMENT_SPLITTER + sensor?.id
                        Dynanode children = new Dynanode(childrenID, sensor?.name , isSelected.get(childrenID), null, null, null)
                        childrens.add(children)
                        sensors.add(job.sensor)
                    }
                }
                
                openChildren.put(sample.id + "", childrens)
                
            } else {
                // Multi selected open nodes (Array) only samples can be open
                for(id in openNodes) {
                    if(!id.contains(TREE_SPLITTER)) {
                        childrens = new ArrayList()
                        sample = Sample.get(new Long(id))
                        sensors.clear()
                        results = Job.findAllBySample(sample)
                   
                        for (job in results) {
                            if(!sensors.contains(job.sensor)) {
                                Sensor sensor = job.sensor
                                String childrenID = sample.id + ELEMENT_SPLITTER + sensor?.id
                                Dynanode children = new Dynanode(childrenID, sensor?.name , isSelected.get(childrenID), null, null, null)
                                childrens.add(children)
                                sensors.add(job.sensor)
                            }
                        }
                        
                        openChildren.put(sample.id + "", childrens)
                    }
                }
            }
        }
        
        return openChildren
    }
    
    /**
     * Split an node from an dynatree.
     * 
     * @params nodeID an node id from like "sampleID.sensorID"
     * 
     * @return if only the node id is a single number the sample id will be
     * returned if an point exists an map with all sample and sensor etc. ids
     * will be returned
     **/
    private Object splitNodeID(String nodeID) {        
        if(nodeID.contains(TREE_SPLITTER)) {
            Map idMap = new HashMap()
            String[] ids = nodeID.split("\\" + TREE_SPLITTER)
            idMap.put(KEY_SAMPLE , ids[ARRAY_SAMPLE])
            idMap.put(KEY_SENSOR , ids[ARRAY_SENSOR])
            return idMap           
        } else {
            return nodeID
        }    
    }
    
    /**
     * Get an Map with all selected nodes from an Dynatree,
     * 
     * @params selecedNodes an String with an single checked node or an Array
     * of selectedNodes that are checked.
     * 
     * @return an map with all selected nodes. Key value is the id from the 
     * node and value the children true
     **/
    private Map getSelectedNodes(Object selectedNodes) {
        Map isSelected = new HashMap()
        if(selectedNodes) {
            if(selectedNodes instanceof String) {
                // Single open node
                isSelected.put(selectedNodes, true)
            } else {
                // Multi selected open nodes (Array)
                for(id in selectedNodes) {
                    isSelected.put(id, true)
                }
            }
        }
        return isSelected
    }
    
    /**
     * Generates logfile link for the frontend to show them.
     *
     * @params entity an Job Entity
     * 
     * @return gets an logfile input if exists else empty
     **/
    private String getLogFile(Job entity) {
        String result = ''
                
        if (entity.errorLog) {
            result += g.htmlTag(tag : 'a', attributes : [
                "href=\"\"",
                "onClick=\"showLog(" + entity.id + ");return false;\""
                ], containsEndTag : true ,innerHtml : 'Anzeigen')
        }

        if (entity.reportFileName) {
            if (result) {
                result += "<br />"
            }
            result += g.htmlTag(tag : 'a', attributes : [
                    "href=" + entity.getReportFileUrl(),
                    "target=_blank"
                ], containsEndTag : true ,innerHtml : 'Report')
        }

        if (entity.logFileName) {
            if (result) {
                result += "<br />"
            }
            result += g.htmlTag(tag : 'a', attributes : [
                    "href=" + entity.getLogFileUrl(),
                    "target=_blank"
                ], containsEndTag : true ,innerHtml : 'cuckoo.log')
        }

        return result
    }
}
