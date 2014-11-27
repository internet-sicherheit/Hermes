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
 * Integration test class for his hypervisor controller.
 *
 * @author Andreas Sekulski
 */
class HypervisorControllerTests extends GroovyTestCase  {
    
    def g = new ApplicationTagLib()

    void testCreate(){
        def controller = new HypervisorController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/hypervisor/Hypervisor')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.class == Hypervisor
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'Hypervisor', action: 'save')
    }   
    
    void testTemplateExist() {
        def controller = new HypervisorController()
        controller.params.name = "Hypervisor"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["Hypervisor"])
    }
    
    void testTemplateNotExist() {
        def controller = new HypervisorController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["null"]))
    }
    
    void testSaveValidData() {
        def controller = new HypervisorController()
        Map postParams = IFPP.initHypervisorParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        assert Hypervisor.getAll().size() == 0
        controller.save()
        assert Hypervisor.getAll().size() == 1
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), Hypervisor.getAll().get(0)])
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveInvalidData() {
        def controller = new HypervisorController()
        
        assert Hypervisor.getAll().size() == 0
        controller.save()
        assert Hypervisor.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testEditHypervisorExists() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.getAll().size() == 1
        controller.params.id = hypervisor.id
        controller.edit()
        
        assert controller.modelAndView.viewName.equals('/hypervisor/Hypervisor')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.equals(hypervisor)
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'Hypervisor', action: 'update')        
    }
    
    void testEditHypervisorExistsNot() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
            
        controller.edit()
        assert Hypervisor.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), null])
        controller.response.reset()
        
        controller.params.id = 0
        controller.edit()
        assert Hypervisor.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), 0])
    }    
    
    void testUpdateValidParams() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.getAll().size() == 1
        Map postParams = IFPP.initHypervisorParams(hypervisor)
        
        for (String key : postParams.keySet()) {
                controller.params.put(key, postParams.get(key))
        }
        
        controller.update()
                
        assert controller.response.json.size() == 4
        assert controller.response.json.isUpdate == true
        assert controller.response.json.id == hypervisor.id
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), hypervisor])
    }
    
    void testUpdateInvalidParams() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.getAll().size() == 1
        Map postParams = IFPP.initHypervisorParams(hypervisor)
        
        for (String key : postParams.keySet()) {
            if(!(key.equals("nameHypervisor"))) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testUpdateVersionError() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.getAll().size() == 1
        Map postParams = IFPP.initHypervisorParams(hypervisor)
        
        for (String key : postParams.keySet()) {
            if(key.equals("hypervisorVersion")) {
                controller.params.put(key, "-1")
            } else {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.VERSION_ERROR_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
        assert controller.response.json.version == hypervisor.version
    }
    
    void testUpdateHypervisorNotFound() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
            
        controller.update()
        assert Hypervisor.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), null])
    }
        
    void testDeleteHypervisorExist() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.getAll().size() == 1
        
        controller.params.id = hypervisor.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), hypervisor])
        assert Hypervisor.getAll().size() == 0
    }
    
    void testDeleteHypervisorNotExists() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), null])
    }
    
    void testDeleteHypervisorVMDeletionInvalid() {
        def controller = new HypervisorController()
        assert Hypervisor.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        Hypervisor hypervisor = vm.hypervisor
        assert Hypervisor.getAll().size() == 1
        
        controller.params.id = hypervisor.id
        controller.delete()
        assert Hypervisor.getAll().size() == 1
    }

    void testDataTable() {
        def controller = new HypervisorController()
        assert Hypervisor.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 2
        controller.response.reset()
        
        hypervisor.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}