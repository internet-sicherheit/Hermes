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

import net.ifis.ites.hermes.InitLoginParameters
import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.util.Dynatree
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Integration test for an jobstatus service.
 *
 * @author Andreas Sekulski
 **/
class JobstatusServiceIntTests extends GroovyTestCase {
    
    def jobstatusService
    
    def g = new ApplicationTagLib()
    
    void testGetJobsByValidType() {

        List pjb
        pjb = jobstatusService.getJobsByType('All')
        assert pjb.size() == 0;

        def node = Node.build()

        def states = [
            'Published' : Job.JobState.PUBLISHED, 
            'Submitted' : Job.JobState.SUBMITTED,
            'Processing' : Job.JobState.PROCESSING, 
            'Success' : Job.JobState.SUCCESS,
            'Failure' : Job.JobState.FAILURE,
            'Pending' : Job.JobState.PENDING
        ]

        for (state in states ) {
            pjb = jobstatusService.getJobsByType(state.key)
            assert pjb.size() == 0;
            
            Job.build(state: state.value, node: node)
            pjb = jobstatusService.getJobsByType(state.key)
            assert pjb.size() == 1;
        }
        
        pjb = jobstatusService.getJobsByType('Active')
        assert pjb.size() == Job.JobState.values().size() - 2;
        
        pjb = jobstatusService.getJobsByType('All')
        assert pjb.size() == Job.JobState.values().size();
    }
    
    void testGetJobsByInvalidType() {
        List pjb = jobstatusService.getJobsByType(null)
        assert pjb.size() == 0;
    }

    void testBuildTree() {
        Dynatree tree
        
        // Test Empy Tree
        tree = jobstatusService.buildTree(null, null)
        assert tree.root.size() == 0
        
        // Test Tree with Sample node
        Sample sample = Sample.build()
        tree = jobstatusService.buildTree(null, null)
        assert tree.root.size() == 1
        for (node in tree.root) {
            assert new Long(node.key) == sample.id
            assert node.value == sample.name
            assert node.isSelected == null
            assert node.isFolder == true
            assert node.isLazy == true
            assert node.childrens.size() == 0
        }
        
        // Test Tree with selected Sample Node
        Object selectedNodes = [sample.id + ""]
        tree = jobstatusService.buildTree(null, selectedNodes)
        assert tree.root.size() == 1
        for (node in tree.root) {
            assert new Long(node.key) == sample.id
            assert node.value == sample.name
            assert node.isSelected == true
            assert node.isFolder == true
            assert node.isLazy == true
            assert node.childrens.size() == 0
        }
        
        // Test Tree with open Sample node
        Object openNodes = [sample.id + ""]
        tree = jobstatusService.buildTree(openNodes, null)
        assert tree.root.size() == 1
        for (node in tree.root) {
            assert node.childrens.size() == 0
        }
        
        // Test Tree with open node and children
        Job Job = Job.build()
        sample.delete()
        openNodes = [Job.sample.id + ""]
        
        tree = jobstatusService.buildTree(openNodes, null)
        assert tree.root.size() == 1
        for (node in tree.root) {
            assert node.childrens != null
            assert node.childrens.size == 1
        }
        
        // Test Tree with open node and selected children
        selectedNodes = [Job.sample.id + "." + Job.sensor?.id]
        
        tree = jobstatusService.buildTree(openNodes, selectedNodes)
        assert tree.root.size() == 1
        for (node in tree.root) {
            assert node.childrens != null
            assert node.childrens.size == 1
            for (child in node.childrens) {
                child.isSelected = true
            }
        }
    }
    
    void testGetChildren() {
        List result
        
        // Test get children empty params
        result = jobstatusService.getChildren(null, null)
        assert result.size() == 0
        
        // Test get children invalid params
        result = jobstatusService.getChildren(null, true)
        assert result.size() == 0

        result = jobstatusService.getChildren(null, false)
        assert result.size() == 0
        
        Sample sample = Sample.build()
        result = jobstatusService.getChildren(sample.id, null)
        assert result.size() == 0
        
        // Test get children empty children
        result = jobstatusService.getChildren(sample.id, true)
        assert result.size() == 0
        
        result = jobstatusService.getChildren(sample.id, false)
        assert result.size() == 0
        
        // Test get children with an children element
        Job job = Job.build()
        
        result = jobstatusService.getChildren(job.sample.id, null)
        assert result.size() == 1
        
        result = jobstatusService.getChildren(job.sample.id, true)
        assert result.size() == 1
        
        result = jobstatusService.getChildren(job.sample.id, false)
        assert result.size() == 1
    }
    
    void testGetJobsByEmptyFilter() {
        List result
        
        result = jobstatusService.getJobsByFilter(null)
        assert result.isEmpty() == true
        
        result = jobstatusService.getJobsByFilter(new HashMap())
        assert result.isEmpty() == true
    }
    
    void testGetJobsByFilter() {
        InitLoginParameters.initLoginData()
        List result
        Map nodeSelection = new HashMap()
        Job job = Job.build(sample: Sample.build(), sensor: Sensor.build())
        
        nodeSelection.put(job.sample.id, [job.sensor.id])
        SpringSecurityUtils.doWithAuth(InitLoginParameters.su.username) {
            result = jobstatusService.getJobsByFilter(nodeSelection)
        }

        assert result.size() == 1
    }
        
    void testConvertTree() {
        Map result
        String selectedNodes = ""
        
        result = jobstatusService.convertTree(null)
        assert result.isEmpty() == true        
        
        result = jobstatusService.convertTree(selectedNodes)
        assert result.isEmpty() == true    
        
        selectedNodes = "18,19"
        result = jobstatusService.convertTree(selectedNodes)
        assert result.isEmpty() == false        
        assert result.keySet().size() == 2
        
        selectedNodes = "18,18.19,19,20.21,20.22"
        result = jobstatusService.convertTree(selectedNodes)
        assert result.isEmpty() == false        
        assert result.keySet().size() == 3
        assert result.get("18").size() == 1
        assert result.get("19").size() == 0
        assert result.get("20").size() == 2          
    }
}
