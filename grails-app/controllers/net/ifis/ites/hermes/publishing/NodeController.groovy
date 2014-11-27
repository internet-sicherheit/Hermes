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
import net.ifis.ites.hermes.management.Job
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import org.grails.datastore.mapping.query.api.Criteria

/**
 * Controller to handle all incoming requests for an Node.
 * 
 * @author Andreas Sekulski
 **/
class NodeController {
    
    /**
     * Injected data table service
     **/
    def dataTableService
    
    /**
     * Defined static color code for progressbar
     **/
    private static final Map COLOR_CODE_PERCENT = [
        "low" : "lime",
        "medium" : "orange",
        "high" : "tomato"
    ]  
    
    /**
     * Static mapping from NodeState enums to a string value.
     */
    private static final Map NODE_STATE_MAPPING = [
        (Node.NodeState.ONLINE) : "default.node.state.online",
        (Node.NodeState.OFFLINE) : "default.node.state.offline",
        (Node.NodeState.FAILURE) : "default.node.state.failure",
        (Node.NodeState.INIT) : "default.node.state.init",
        (Node.NodeState.UNKNOWN) : "default.node.state.unknown"
    ]
    
    /**
     * Default node view page
     **/
    def index() {
        render(view : 'index')
    }

    /**
     * Gets all Nodes as an data table json.
     **/
    def dataTable() {
        HashMap aaData = new HashMap()
        List jsonNode = new ArrayList()

        float avg1
        float avg5
        float avg15

        for (node in Node.list()) {

            Criteria c = NodeStatusData.createCriteria()
            NodeStatusData statusData = c.get {
                eq "node", node
                order "timestamp", "desc"
                maxResults 1
            }

            String linkName = node.name
            Node.NodeState state = node.currentState
            
            if(!state) {
                state = Node.NodeState.UNKNOWN
            } else if(state == Node.NodeState.ONLINE) {
                linkName = g.htmlTag(tag : 'a', 
                    attributes : [
                        "href=''",
                        "onClick='drawNodePlot(" + node.id + ");return false;'"
                    ], 
                    containsEndTag : true ,
                    innerHtml : node.name)
            }
            
            // If data is collected and online         
            if(statusData && state == Node.NodeState.ONLINE) {
                avg1 = statusData.cpuLoadAvg1
                avg5 = statusData.cpuLoadAvg5
                avg15 = statusData.cpuLoadAvg15
            } else {
                avg1 = 0
                avg5 = 0
                avg15 = 0
            }
			
            jsonNode.add([
                    (g.message(code:C.DEFAULT_NAME_LABEL_CODE)) : linkName,
                    (g.message(code:C.DEFAULT_STATE_LABEL)) : getNodeStateString(state),
                    (g.message(code:C.DEFAULT_IP_LABEL)) : node.networkAddress,
                    (g.message(code:C.DEFAULT_META_LABEL_CODE)) : node.metadata,   
                    (g.message(code:C.HYPERVISOR_LABEL_CODE)) : node.hypervisors.toString(), // TODO: fix for multiple hypervisors
                    (g.message(code:C.DEFAULT_CPU_AVG_LABEL, args: ["1 Min"])) : generateProgressBar(avg1),
                    (g.message(code:C.DEFAULT_CPU_AVG_LABEL, args: ["5 Min"])) : generateProgressBar(avg5),
                    (g.message(code:C.DEFAULT_CPU_AVG_LABEL, args: ["15 Min"])) : generateProgressBar(avg15)
                ]) 
        }

        aaData.put('aaData', jsonNode)
                        
        return render(aaData as JSON)
    }
	
    /**
     * Inits an workload from an given node.
     * @param id Identity number from Node
     * @param max Maximum value to get data from db
     * @return an AVG initial vector list from an node
     */
    def initWorkload(Long id, Integer max) {
		
        Node node = Node.get(id)
		
        List listavg1 = new ArrayList()
        List listavg5 = new ArrayList()
        List listavg15 = new ArrayList()
		
        if(!node) {
            return render(text:g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.NODE_LABEL_CODE), id]), status: SEC.NOT_FOUND_CODE)
        }
		
        if(!max) {
            max = new Integer(300)
        }
		
        Criteria c = NodeStatusData.createCriteria()
        List statusDatas = c.list {
            eq "node", node
            order "timestamp", "desc"
            maxResults max
        }
		
        Collections.reverse(statusDatas);
		
        for(statusData in statusDatas) {
            float avg1 = statusData.cpuLoadAvg1 * 100
            float avg5 = statusData.cpuLoadAvg5 * 100
            float avg15 = statusData.cpuLoadAvg15 * 100
						
            listavg1.add(checkValue(avg1))
            listavg5.add(checkValue(avg5))
            listavg15.add(checkValue(avg15))
        }
		
        return render(contentType:"text/json") {[
                avg1: listavg1,
                avg5: listavg5,
                avg15: listavg15	
            ]}
    }
	
    /**
     * Gets an current workload from an node if exists
     * else an error.
     * 
     * @param id Identity from an node
     * @return Nodeinformation in an JSON contains avg and current state etc.
     */
    def nodeWorkload(Long id) {
        
        Node node = Node.get(id)
        
        if(!node) {
            return render(text:g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.NODE_LABEL_CODE), id]), status: SEC.NOT_FOUND_CODE)
        }
        
        Criteria c = NodeStatusData.createCriteria()
        NodeStatusData statusData = c.get {
            eq "node", node
            order "timestamp", "desc"
            maxResults 1
        }
		
        Job pj = Job.findByNodeAndState(node, Job.JobState.SUBMITTED)
		
        String job = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Job', g.message(code: C.DEFAULT_TITLE_EMPTY)])
        String sample = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sample', g.message(code: C.DEFAULT_TITLE_EMPTY)])
        String sensor = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sensor', g.message(code: C.DEFAULT_TITLE_EMPTY)])
		
        if(pj) {
            job = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Job',pj.name])
            sample = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sample', pj.sample.name])
            if(pj.sensor) {
                sensor = g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sensor', pj.sensor.name])
            }
        }
		
        float avg1 = statusData.cpuLoadAvg1 * 100
        float avg5 = statusData.cpuLoadAvg5 * 100
        float avg15 = statusData.cpuLoadAvg15 * 100
						        
        return render(contentType:"text/json") {[
                node: [
                    name: g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Node', node.name]),
                    ip : g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['IP', node.networkAddress]),
                    state: g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Nodestate', getNodeStateString(node.currentState)]),
                    hypervisor: g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Hypervisor', node.hypervisors.getAt(0)]),
                    job: job,
                    sample: sample,
                    sensor: sensor
                ],	
                avg : [
                    avg1: [
                        value: checkValue(avg1),
                        label: "avg1"
                    ], 
                    avg5: [
                        value: checkValue(avg5),
                        label: "avg5"
                    ], 
                    avg15: [
                        value: checkValue(avg15),
                        label: "avg15"
                    ]
                ]
            ]}
    }
    
    /**
     * Returns an initialization mapping from an node data table as an JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "Node", action: "dataTable"),
                "aoColumns": [
                ["mData": g.message(code: C.DEFAULT_NAME_LABEL_CODE)],
                ["mData": g.message(code: C.DEFAULT_STATE_LABEL)],
                ["mData": g.message(code: C.DEFAULT_IP_LABEL)],
                ["mData": g.message(code: C.DEFAULT_META_LABEL_CODE)],
                ["mData": g.message(code: C.HYPERVISOR_LABEL_CODE)],
                ["mData": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["1 Min"])],
                ["mData": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["5 Min"])],
                ["mData": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["15 Min"])],
            ],
                "aoColumnDefs": [
                ["sTitle": g.message(code: C.DEFAULT_NAME_LABEL_CODE), "aTargets": [0]],
                ["sTitle": g.message(code: C.DEFAULT_STATE_LABEL), "aTargets": [1]],
                ["sTitle": g.message(code: C.DEFAULT_IP_LABEL), "aTargets": [2]],
                ["sTitle": g.message(code: C.DEFAULT_META_LABEL_CODE), "aTargets": [3]],
                ["sTitle": g.message(code: C.HYPERVISOR_LABEL_CODE), "aTargets": [4]],
                ["sTitle": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["1 Min"]), "aTargets": [5]],
                ["sTitle": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["5 Min"]), "aTargets": [6]],
                ["sTitle": g.message(code: C.DEFAULT_CPU_AVG_LABEL, args:["15 Min"]), "aTargets": [7]],
            ],
                "aaSorting": [[1, "desc"]]
        ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
	
    /**
     * Checks if value is greater than 100 or lower than 0
     * @param value Value to check
     * @return value 0 if lower than 0 else value else 100 if higher value
     */
    private float checkValue(float value) {
        if(value > 100) {
            return 100
        }
		
        if(value < 0) {
            return 0
        }
		
        return Math.round(value)
    }
    
    /**
     * Generates an Progressbar from the given value.
     * 
     * @params value from the percent in decimal 0,XX
     * @return an progressbar html element.
     **/
    private String generateProgressBar(float value) {
        
        String color
        
        long percent = Math.round(value * 100)
        
        int percentStyle = percent;
        
        if(percent < 50) {
            color = COLOR_CODE_PERCENT.get("low")
        } else if(percent < 80) {
            color = COLOR_CODE_PERCENT.get("medium")
        } else {
            color = COLOR_CODE_PERCENT.get("high")
        }
        
        if(percent > 100) {
            percentStyle = 100
        }
        
        return g.htmlTag(tag : 'div', attributes : [
                "class=\"progress\"",
            ], 
            containsEndTag : true ,
            innerHtml : g.htmlTag(
                tag : 'span',
                contrainsEndTag : true,
                innerHtml : percent + "%",
                attributes : [
                    "class=\"meter text-center\"",
                    "style=\"width:" + percentStyle + "%;background:" + color + ";\""
                ]
            ))
    }

    /**
     * Gets the string representation for the given NodeState enum.
     *
     * @param state
     *      The NodeState for which to get the string.
     * @return
     *      A string representing the given state.
     */
    private String getNodeStateString(Node.NodeState state) {
        return g.message(code: NODE_STATE_MAPPING[state])
    }
}