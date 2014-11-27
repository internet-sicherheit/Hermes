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

import net.ifis.ites.hermes.management.Job
import net.ifis.ites.hermes.management.Sample
import net.ifis.ites.hermes.management.Sensor
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Integration test for an jobstatus controller
 *
 * @author Andreas Sekulski 
 */
class JobstatusControllerTests extends GroovyTestCase {
    
    def g = new ApplicationTagLib()
    
    void testIndex() {
        def controller = new JobstatusController()
        controller.index()
        assertEquals('/jobstatus/jobstatus', controller.response.redirectedUrl)
    }
    
    void testAllJobsValidTypeParam() {
        def controller = new JobstatusController()
        
        controller.allJobs()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()

        controller.params.type = 'All'
        controller.allJobs()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()

        Job job = Job.build()

        controller.params.type = 'All'
        controller.allJobs()
        assert controller.response.json.aaData.size() == 1
        controller.response.reset()
    }
    
    void testAllJobsInvalidTypeParam() {
        def controller = new JobstatusController()
        
        controller.allJobs()
        assert controller.response.json.aaData.size() == 0
    }
    
    void testRenderDynatree() {
        def result
        def controller = new JobstatusController()
        controller.renderDynatree()
        assert controller.response.json.isEmpty() == true
        controller.response.reset()
        
        Sample sample = Sample.build()
        controller.renderDynatree()
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        assert result.title == sample.name
        assert result.isFolder == true
        assert result.isLazy == true
        assert result.key == sample.id + ""
        controller.response.reset()
        
        controller.params."openNodes[]" = [sample.id + ""]
        controller.renderDynatree()
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        assert result.expand == true
        controller.response.reset()
        
        controller.params."selectedNodes[]" = [sample.id + ""]
        controller.renderDynatree()        
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        assert result.select == true
        controller.response.reset()
    }
    
    void testRenderChildren() {
        def result
        def controller = new JobstatusController()
        
        controller.renderChildren()
        assert controller.response.json.isEmpty() == true
        controller.response.reset()
        
        Sample sample = Sample.build()
        controller.params.id = sample.id
        controller.renderChildren()
        assert controller.response.json.isEmpty() == true
        controller.response.reset()
        
        Job job = Job.build(sensor: Sensor.build())
        controller.params.id = job.sample.id
        controller.renderChildren()
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        result.name == "name"
        result.expand == true
        result.key == job.sample.id + "." + job.sensor.id        
        controller.response.reset()
        
        controller.params.id = job.sample.id
        controller.params.selected = false
        controller.renderChildren()
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        result.name == "name"
        result.expand == true
        result.select == false
        result.key == job.sample.id + "." + job.sensor.id        
        controller.response.reset()
        
        controller.params.id = job.sample.id
        controller.params.selected = true
        controller.renderChildren()
        assert controller.response.json.isEmpty() == false
        assert controller.response.json.size() == 1
        result = controller.response.json[0]
        result.name == "name"
        result.expand == true
        result.select == true
        result.key == job.sample.id + "." + job.sensor.id        
        controller.response.reset()
    }
    
    void testRenderFilterJobs() {
        def renderMap
        def controller = new JobstatusController()
        
        controller.metaClass.render = { Map map ->
            renderMap = map
        }
        
        controller.renderFilterJobs()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()

        controller.params.nodeSelection = ""
        controller.renderFilterJobs()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()

        controller.params.nodeSelection = "18"
        controller.renderFilterJobs()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        Job job = Job.build(sensor: Sensor.build())
        controller.params.nodeSelection = job.sample.id + ""
        controller.renderFilterJobs()
        assert controller.response.json.aaData.size() == 1
        controller.response.reset()
    }
        
    void testJobstatus() {
        def controller = new JobstatusController()
        controller.jobstatus()
        assertEquals('/jobstatus/jobstatus', controller.modelAndView.viewName)
    }
    
    void testDetail() {
        def controller = new JobstatusController()
        // Test detail view with empty params
        controller.detail()
        
        assert controller.response.text.equals(g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.JOB_LABEL_CODE), null]))  
        controller.response.reset()   
            
        def pj = Job.build()
        controller.params.id = pj.id
        controller.detail() 
        assert controller.response.text.equals('')   
        controller.response.reset()   
        
        pj = Job.build(errorLog:"ErrorLog")
        controller.params.id = pj.id
        controller.detail()
        assert controller.response.text.equals('ErrorLog') 
        controller.response.reset()  
    }
    
    void testJobdetail() {
        def controller = new JobstatusController()
        // Test detail view with empty params
        controller.jobdetail()
        
        assert controller.response.text.equals(g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.JOB_LABEL_CODE), null]))  
        controller.response.reset() 
        
        controller.params.id = 0
        controller.jobdetail() 
        assert controller.response.text.equals(g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [g.message(code: C.JOB_LABEL_CODE), 0]))  
        controller.response.reset() 
        
        def job = Job.build()
        controller.params.id = job.id
        controller.jobdetail() 
        assert controller.response.text.isEmpty() == false   
        controller.response.reset()   
    }
}