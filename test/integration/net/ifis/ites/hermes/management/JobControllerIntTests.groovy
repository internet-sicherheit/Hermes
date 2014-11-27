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
import net.ifis.ites.hermes.InitFormPostParameter as IFPP
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Integration test class for his job controller.
 *
 * @author Andreas Sekulski
 */
class JobControllerIntTests extends GroovyTestCase {

    def g = new ApplicationTagLib()
    
    void testCreate(){
        def controller = new JobController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/job/create')
        assert controller.modelAndView.model.size() == 5
        assert controller.modelAndView.model.mapGroup.size() == 0
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.JOB_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'Job', action: 'save')
        assert controller.modelAndView.model.isCreateForm == true
    }   
    
    void testTemplateExist() {
        def controller = new JobController()
        controller.params.name = "create"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["Job"])
    }
    
    void testTemplateNotExist() {
        def controller = new JobController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["null"]))
    }
    
    void testSaveValidData() {
        def controller = new JobController()
        Map postParams = IFPP.initJobParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }

        assert Job.getAll().size() == 0
        controller.save()
        assert Job.getAll().size() == 1
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_ALL_DATA_CREATE)
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveCrossData() {
        def controller = new JobController()
        Map postParams = IFPP.initJobParams(null)
        
        postParams."vm-hypervisor-multi".add(VirtualMachine.build().id)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }

        assert Job.getAll().size() == 0
        controller.save()
        assert Job.getAll().size() == 2
    }
    
    void testSaveEmptyData() {
        def controller = new JobController()
        
        assert Job.getAll().size() == 0
        controller.save()
        assert Job.getAll().size() == 0
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_ALL_DATA_CREATE)
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveInvalidSample() {
        def controller = new JobController()
        Map postParams = IFPP.initJobParams(null)
        Sample sample
        
        for (String key : postParams.keySet()) {
            if(!key.equals("sample-multi")) {
                controller.params.put(key, postParams.get(key))
            } else {
                sample = Sample.get(postParams.get(key).get(0))
                controller.params.put(key, postParams.get(key))
                sample.delete(flush : true)
            }
        }
        
        controller.save()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), sample.id]) + "<br>"
    }
    
    void testSaveInvalidVM() {
        def controller = new JobController()
        Map postParams = IFPP.initJobParams(null)
        VirtualMachine vm
        
        for (String key : postParams.keySet()) {
            if(!key.equals("vm-hypervisor-multi")) {
                controller.params.put(key, postParams.get(key))
            } else {
                vm = VirtualMachine.get(postParams.get(key))
                controller.params.put(key, postParams.get(key))
                vm.delete(flush : true)
            }
        }
        
        controller.save()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), vm.id]) + "<br>"
    }    
    
    void testSave_sensorIsNull_messageOk() {
        def controller = new JobController()
        Map postParams = IFPP.initJobParams(null)
        Sensor sensor
        
        for (String key : postParams.keySet()) {
            if(!key.equals("sensor-multi")) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.save()
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_ALL_DATA_CREATE)
        assert controller.response.json.isUpdate == false
    }
    
    void testDeleteJobExist() {
        def controller = new JobController()
        assert Job.getAll().size() == 0
        Job job = Job.build()
        assert Job.getAll().size() == 1
        
        controller.params.id = job.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.JOB_LABEL_CODE), job])
        assert Job.getAll().size() == 0
    }
    
    void testDeleteJobNotExists() {
        def controller = new JobController()
        assert Job.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.JOB_LABEL_CODE), null])
    }
    
    void testDataTable() {
        def controller = new JobController()
        assert Job.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        Job job = Job.build()
        assert Job.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 11
        controller.response.reset()
        
        job.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}
