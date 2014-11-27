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

import net.ifis.ites.hermes.management.Sample
import net.ifis.ites.hermes.management.Sensor
import net.ifis.ites.hermes.management.Hypervisor;
import net.ifis.ites.hermes.management.Job
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Intergration tests for an node controller
 *
 * @author Andreas Sekulski
 **/
class NodeControllerTests extends GroovyTestCase {
	
	def g = new ApplicationTagLib()
	
	void testIndex() {
		def controller = new NodeController()
		controller.index()
		assertEquals('/node/index', controller.modelAndView.viewName)
	}
	
    void testDataTable() {
        def controller = new NodeController()
        
        assert Node.list().size() == 0
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        Node node = Node.build()
        assert Node.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 8
        controller.response.reset()

        node.delete(flush:true)
        assert Node.list().size() == 0
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
    }
	
	void testInitWorkloadNodeNotFound() {
		def controller = new NodeController()
		controller.initWorkload()
		assert controller.response.status == SEC.NOT_FOUND_CODE
		assert controller.response.text == g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.NODE_LABEL_CODE), null])
	}
	
	void testInitWorkloadMaxNotSet() {
		def controller = new NodeController()
		
		Node node = Node.build()
		
		for(int i = 0; i < 300; i++) {
			NodeStatusData.build(node: node, cpuLoadAvg1: 1, cpuLoadAvg5: 5, cpuLoadAvg15: 15)
		}

		assert NodeStatusData.list().size() == 300
		
		controller.params.id = node.id
		controller.initWorkload()
		
		assert controller.response.json.size() == 3
		assert controller.response.json.avg1.size() == 300
		assert controller.response.json.avg5.size() == 300
		assert controller.response.json.avg15.size() == 300
	}
	
	void testInitWorkloadNotEnoughData() {
		int dataSitze = 200
		def controller = new NodeController()
		
		Node node = Node.build()
		
		for(int i = 0; i < 200; i++) {
			NodeStatusData.build(node: node, cpuLoadAvg1: 1, cpuLoadAvg5: 5, cpuLoadAvg15: 15)
		}

		assert NodeStatusData.list().size() == dataSitze
		
		controller.params.id = node.id
		controller.initWorkload()
		
		assert controller.response.json.size() == 3
		assert controller.response.json.avg1.size() == dataSitze
		assert controller.response.json.avg5.size() == dataSitze
		assert controller.response.json.avg15.size() == dataSitze
	}
	
	void testInitWorkloadNodeFound() {
		int dataSitze = 100
		def controller = new NodeController()
		
		Node node = Node.build()
		
		for(int i = 0; i < 300; i++) {
			NodeStatusData.build(node: node, cpuLoadAvg1: 1, cpuLoadAvg5: 5, cpuLoadAvg15: 15)
		}

		assert NodeStatusData.list().size() == 300
		
		controller.params.id = node.id
		controller.params.max = dataSitze
		controller.initWorkload()
		
		assert controller.response.json.size() == 3
		assert controller.response.json.avg1.size() == dataSitze
		assert controller.response.json.avg5.size() == dataSitze
		assert controller.response.json.avg15.size() == dataSitze
	}
	
	void testWorkloadNodeNotFound() {
		def controller = new NodeController()
		controller.nodeWorkload()
		assert controller.response.status == SEC.NOT_FOUND_CODE
		assert controller.response.text == g.message(
					code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE,
					args: [g.message(code: C.NODE_LABEL_CODE), null])
	}
	
	void testWorkloadNodeFoundNoJob() {
		def controller = new NodeController()
		Hypervisor hy = Hypervisor.build()
		Node node = Node.build(hypervisors : [hy])
		NodeStatusData nss = NodeStatusData.build(node: node, cpuLoadAvg1: 0.01, cpuLoadAvg5: 0.05, cpuLoadAvg15: 0.15)
		
		controller.params.id = node.id
		controller.nodeWorkload()
		
		assert controller.response.json.node.job == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Job', g.message(code: C.DEFAULT_TITLE_EMPTY)])
		assert controller.response.json.node.sensor ==  g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sensor', g.message(code: C.DEFAULT_TITLE_EMPTY)])
		assert controller.response.json.node.sample ==  g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sample', g.message(code: C.DEFAULT_TITLE_EMPTY)])
		assert controller.response.json.node.hypervisor == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Hypervisor', hy.name])
		assert controller.response.json.node.name == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Node', node.name])
		assert controller.response.json.node.state == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Nodestate', g.message(code : 'default.node.state.init')])
		assert controller.response.json.node.ip == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['IP', node.networkAddress])				
	
		assert controller.response.json.avg.avg1.value == Math.round(nss.cpuLoadAvg1 * 100)
		assert controller.response.json.avg.avg5.value == Math.round(nss.cpuLoadAvg5 * 100)
		assert controller.response.json.avg.avg15.value == Math.round(nss.cpuLoadAvg15 * 100)
		}
	
	void testWorkloadNodeFoundWithJob() {
		def controller = new NodeController()
		Hypervisor hy = Hypervisor.build()
		Node node = Node.build(hypervisors : [hy])
		
		Job job = Job.build(sample : Sample.build(), sensor: Sensor.build(), node: node, state: Job.JobState.SUBMITTED)

		NodeStatusData nss = NodeStatusData.build(node: node, cpuLoadAvg1: 0.01, cpuLoadAvg5: 0.05, cpuLoadAvg15: 0.15)
		
		controller.params.id = node.id
		controller.nodeWorkload()
		
		assert controller.response.json.node.job == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Job', job.name])
		assert controller.response.json.node.sensor ==  g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sensor', job.sensor.name])
		assert controller.response.json.node.sample ==  g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Sample', job.sample.name])
		assert controller.response.json.node.hypervisor == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Hypervisor', hy.name])
		assert controller.response.json.node.name == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Node', node.name])
		assert controller.response.json.node.state == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['Nodestate', g.message(code : 'default.node.state.init')])
		assert controller.response.json.node.ip == g.message(code : C.DEFAULT_TITLE_MESSAGE, args:['IP', node.networkAddress])
		
		assert controller.response.json.avg.avg1.value == Math.round(nss.cpuLoadAvg1 * 100)
		assert controller.response.json.avg.avg5.value == Math.round(nss.cpuLoadAvg5 * 100)
		assert controller.response.json.avg.avg15.value == Math.round(nss.cpuLoadAvg15 * 100)
	}
}