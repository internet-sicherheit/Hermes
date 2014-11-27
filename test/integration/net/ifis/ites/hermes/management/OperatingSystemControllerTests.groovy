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
 * Integration test class for his operating system controller.
 *
 * @author Andreas Sekulski
 */
class OperatingSystemControllerTests extends GroovyTestCase {

    def g = new ApplicationTagLib()

    void testCreate(){
        def controller = new OperatingSystemController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/operatingSystem/OS')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.class == OperatingSystem
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'OperatingSystem', action: 'save')
    }   
    
    void testTemplateExist() {
        def controller = new OperatingSystemController()
        controller.params.name = "OS"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["OS"])
    }
    
    void testTemplateNotExist() {
        def controller = new OperatingSystemController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["null"]))
    }
    
    void testSaveValidData() {
        def controller = new OperatingSystemController()
        Map postParams = IFPP.initOSParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        assert OperatingSystem.getAll().size() == 0
        controller.save()
        assert OperatingSystem.getAll().size() == 1
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), OperatingSystem.getAll().get(0)])
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveInvalidData() {
        def controller = new OperatingSystemController()
        
        assert OperatingSystem.getAll().size() == 0
        controller.save()
        assert OperatingSystem.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testEditOSExists() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.getAll().size() == 1
        controller.params.id = os.id
        controller.edit()
        
        assert controller.modelAndView.viewName.equals('/operatingSystem/OS')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.equals(os)
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'OperatingSystem', action: 'update')        
    }
    
    void testEditOSExistsNot() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
            
        controller.edit()
        assert OperatingSystem.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), null])
        controller.response.reset()
        
        controller.params.id = 0
        controller.edit()
        assert OperatingSystem.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), 0])
    }
    
    void testUpdateValidParams() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.getAll().size() == 1
        Map postParams = IFPP.initOSParams(os)
        
        for (String key : postParams.keySet()) {
                controller.params.put(key, postParams.get(key))
        }
        
        controller.update()
                
        assert controller.response.json.size() == 4
        assert controller.response.json.isUpdate == true
        assert controller.response.json.id == os.id
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), os])
    }
    
    void testUpdateInvalidParams() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.getAll().size() == 1
        Map postParams = IFPP.initOSParams(os)
        
        for (String key : postParams.keySet()) {
            if(!(key.equals("osName"))) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testUpdateVersionError() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.getAll().size() == 1
        Map postParams = IFPP.initOSParams(os)
        
        for (String key : postParams.keySet()) {
            if(key.equals("operatingSystemVersion")) {
                controller.params.put(key, "-1")
            } else {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.VERSION_ERROR_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
        assert controller.response.json.version == os.version
    }
    
    void testUpdaterOSNotFound() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
            
        controller.update()
        assert OperatingSystem.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), null])
    }
    
    void testDeleteOSExist() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.getAll().size() == 1
        
        controller.params.id = os.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), os])
        assert OperatingSystem.getAll().size() == 0
    }
    
    void testDeleteOSNotExists() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), null])
    }
    
    void testDeleteOSVMDeletionInvalid() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        OperatingSystem os = vm.os
        assert OperatingSystem.getAll().size() == 1
        
        controller.params.id = os.id
        controller.delete()
        assert OperatingSystem.getAll().size() == 1
    }
    
    void testDataTable() {
        def controller = new OperatingSystemController()
        assert OperatingSystem.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        OperatingSystem os = OperatingSystem.build()
        assert OperatingSystem.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 5
        controller.response.reset()
        
        os.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}