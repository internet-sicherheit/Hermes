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
 * Integration test class for his virtual machine controller.
 *
 * @author Andreas Sekulski
 */
class VirtualMachineControllerTests extends GroovyTestCase {

    def g = new ApplicationTagLib()
    
    void testCreate(){
        def controller = new VirtualMachineController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/virtualMachine/VM')
        assert controller.modelAndView.model.size() == 5
        assert controller.modelAndView.model.instance.class == VirtualMachine
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.VM_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'VirtualMachine', action: 'save')
        assert controller.modelAndView.model.dataUploadRequired == true
    }   
    
    void testTemplateExist() {
        def controller = new VirtualMachineController()
        controller.params.name = "VM"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["VM"])
    }
    
    void testTemplateNotExist() {
        def controller = new VirtualMachineController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["null"]))
    }
    
    void testSaveValidData() {
        def controller = new VirtualMachineController()
        Map postParams = IFPP.initVMParams(null, true)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        assert VirtualMachine.getAll().size() == 0
        controller.save()
        assert VirtualMachine.getAll().size() == 1
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), VirtualMachine.getAll().get(0)])
        assert controller.response.json.isUpdate == false
    }
    
    void testSaveInvalidData() {
        def controller = new VirtualMachineController()
        
        assert VirtualMachine.getAll().size() == 0
        controller.save()
        assert VirtualMachine.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_FILE_EXCEPTION)
    }
    
    void testSaveInvalidDataHypervisor() {
        def controller = new VirtualMachineController()
        Map postParams = IFPP.initVMParams(null, false)
        Hypervisor hypervisor
        
        for (String key : postParams.keySet()) {
            if(!key.equals("hypervisor")) {
                controller.params.put(key, postParams.get(key))
            } else {
                hypervisor = postParams.get(key)
                controller.params.put("hypervisor", hypervisor)
                hypervisor.delete(flush : true)
            }
        }
        
        controller.save()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), hypervisor.id])
    }
    
    void testSaveInvalidDataOS() {
        def controller = new VirtualMachineController()
        Map postParams = IFPP.initVMParams(null, false)
        OperatingSystem os
        
        for (String key : postParams.keySet()) {
            if(!key.equals("os")) {
                controller.params.put(key, postParams.get(key))
            } else {
                os = postParams.get(key)
                controller.params.put("os", os)
                os.delete(flush : true)
            }
        }
        
        controller.save()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), os.id])
    }
    
    void testEditVMExists() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.getAll().size() == 1
        controller.params.id = vm.id
        controller.edit()
        
        assert controller.modelAndView.viewName.equals('/virtualMachine/VM')
        assert controller.modelAndView.model.size() == 5
        assert controller.modelAndView.model.instance.equals(vm)
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [g.message(code: C.VM_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'VirtualMachine', action: 'update')        
        assert controller.modelAndView.model.dataUploadRequired == false   
    }
    
    void testEditVMExistsNot() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
            
        controller.edit()
        assert VirtualMachine.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), null])
        controller.response.reset()
        
        controller.params.id = 0
        controller.edit()
        assert VirtualMachine.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), 0])
    }   
    
    void testUpdateValidParams() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.getAll().size() == 1
        Map postParams = IFPP.initVMParams(vm, true)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.update()
                
        assert controller.response.json.size() == 4
        assert controller.response.json.isUpdate == true
        assert controller.response.json.id == vm.id
        assert controller.response.json.statusCode == SEC.OK_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), vm])
    }
    
    void testUpdateInvalidParams() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.getAll().size() == 1
        Map postParams = IFPP.initVMParams(vm , false)
        
        for (String key : postParams.keySet()) {
            if(!(key.equals("vmName"))) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testUpdateVersionError() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.getAll().size() == 1
        Map postParams = IFPP.initVMParams(vm, true)
        
        for (String key : postParams.keySet()) {
            if(key.equals("vmVersion")) {
                controller.params.put(key, "-1")
            } else {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.VERSION_ERROR_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
        assert controller.response.json.version == vm.version
    }
    
    void testUpdateVMNotFound() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
            
        controller.update()
        assert VirtualMachine.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), null])
    }
    
    void testUpdateInvalidHypervisor() {
        def controller = new VirtualMachineController()
        VirtualMachine vm = VirtualMachine.build()
        Map postParams = IFPP.initVMParams(vm, false)
        Hypervisor hypervisor
        
        for (String key : postParams.keySet()) {
            if(!key.equals("hypervisor")) {
                controller.params.put(key, postParams.get(key))
            } else {
                hypervisor = postParams.get(key)
                controller.params.put("hypervisor", hypervisor)
                hypervisor.delete(flush : true)
            }
        }
        
        controller.update()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.HYPERVISOR_LABEL_CODE), hypervisor.id])
   
    }
    
    void testUpdateInvalidOS() {
        def controller = new VirtualMachineController()
        VirtualMachine vm = VirtualMachine.build()
        Map postParams = IFPP.initVMParams(vm, false)
        OperatingSystem os
        
        for (String key : postParams.keySet()) {
            if(!key.equals("os")) {
                controller.params.put(key, postParams.get(key))
            } else {
                os = postParams.get(key)
                controller.params.put("os", os)
                os.delete(flush : true)
            }
        }
        
        controller.update()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.OPERATING_SYSTEM_LABEL_CODE), os.id])
    }
    
    void testDeleteVMExist() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.getAll().size() == 1
        
        controller.params.id = vm.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), vm])
        assert VirtualMachine.getAll().size() == 0
    }
    
    void testDeleteVMNotExists() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.VM_LABEL_CODE), null])
    }
    
    void testDeleteVMJobDeletionInvalid() {
        def controller = new VirtualMachineController()
        assert Sample.getAll().size() == 0
        Job job = Job.build()
        VirtualMachine vm = job.virtualMachine
        assert VirtualMachine.getAll().size() == 1
        
        controller.params.id = vm.id
        controller.delete()
        assert controller.response.status  == SEC.DATA_INTEGRATION_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DATA_INTEGRATION_ERROR_CODE, args: [g.message(code :C.VM_LABEL_CODE), vm])
    }
    
    void testDataTable() {
        def controller = new VirtualMachineController()
        assert VirtualMachine.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        VirtualMachine vm = VirtualMachine.build()
        assert VirtualMachine.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 6
        controller.response.reset()
        
        vm.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}
