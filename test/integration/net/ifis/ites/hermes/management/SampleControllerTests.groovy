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
 * Integration test class for his sample controller.
 *
 * @author Andreas Sekulski
 */
class SampleControllerTests extends GroovyTestCase {
    
    def g = new ApplicationTagLib()

    void testCreate(){
        def controller = new SampleController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/sample/Sample')
        assert controller.modelAndView.model.size() == 5
        assert controller.modelAndView.model.instance.class == Sample
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'Sample', action: 'save')
        assert controller.modelAndView.model.dataUploadRequired == true
    }   
    
    void testTemplateExist() {
        def controller = new SampleController()
        controller.params.name = "Sample"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["Sample"])
    }
    
    void testTemplateNotExist() {
        def controller = new SampleController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["null"]))
    }
    
    void testSaveValidData() {
        def controller = new SampleController()
        Map postParams = IFPP.initSampleParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        assert Sample.getAll().size() == 0
        controller.save()
        assert Sample.getAll().size() == 1
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), Sample.getAll().get(0)])
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveInvalidData() {
        def controller = new SampleController()
        
        assert Sample.getAll().size() == 0
        controller.save()
        assert Sample.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_FILE_EXCEPTION)
    }
    
    void testEditSampleExists() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Sample sample = Sample.build()
        assert Sample.getAll().size() == 1
        controller.params.id = sample.id
        controller.edit()
        
        assert controller.modelAndView.viewName.equals('/sample/Sample')
        assert controller.modelAndView.model.size() == 5
        assert controller.modelAndView.model.instance.equals(sample)
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'Sample', action: 'update')        
        assert controller.modelAndView.model.dataUploadRequired == false   
    }
    
    void testEditSampleExistsNot() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
            
        controller.edit()
        assert Sample.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), null])
        controller.response.reset()
        
        controller.params.id = 0
        controller.edit()
        assert Sample.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), 0])
    }    
    
    void testUpdateValidParams() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Sample sample = Sample.build()
        assert Sample.getAll().size() == 1
        Map postParams = IFPP.initSampleParams(sample)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.update()
                
        assert controller.response.json.size() == 4
        assert controller.response.json.isUpdate == true
        assert controller.response.json.id == sample.id
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), sample])
    }
    
    void testUpdateInvalidParams() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Sample sample = Sample.build()
        assert Sample.getAll().size() == 1
        Map postParams = IFPP.initSampleParams(sample)
        
        for (String key : postParams.keySet()) {
            if(!(key.equals("sampleName"))) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testUpdateVersionError() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Sample sample = Sample.build()
        assert Sample.getAll().size() == 1
        Map postParams = IFPP.initSampleParams(sample)
        
        for (String key : postParams.keySet()) {
            if(key.equals("sampleVersion")) {
                controller.params.put(key, "-1")
            } else {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.VERSION_ERROR_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
        assert controller.response.json.version == sample.version
    }
    
    void testUpdateSampleNotFound() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
            
        controller.update()
        assert Sample.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), null])
    }
    
    void testDeleteSampleExist() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Sample sample = Sample.build()
        assert Sample.getAll().size() == 1
        
        controller.params.id = sample.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), sample])
        assert Sample.getAll().size() == 0
    }
    
    void testDeleteSampleNotExists() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), null])
    }
    
    void testDeleteSampleJobDeletionInvalid() {
        def controller = new SampleController()
        assert Sample.getAll().size() == 0
        Job job = Job.build()
        Sample sample = job.sample
        assert Sample.getAll().size() == 1
        
        controller.params.id = sample.id
        controller.delete()
        assert controller.response.status  == SEC.DATA_INTEGRATION_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DATA_INTEGRATION_ERROR_CODE, args: [g.message(code :C.SAMPLE_LABEL_CODE), sample])
    }
    
    void testDataTable() {
        def controller = new SampleController()
        assert Sample.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        Sample sample = Sample.build()
        assert Sample.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 9
        controller.response.reset()
        
        sample.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}