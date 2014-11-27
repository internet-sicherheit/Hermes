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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ifis.ites.hermes.security

import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.InitFormPostParameter as IFPP
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.InitLoginParameters as ILP
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Integration test class for his hypervisor controller.
 *
 * @author Andreas Sekulski
 */
class UserControllerTests extends GroovyTestCase  {
    
    def g = new ApplicationTagLib()

    void testCreate(){
        def controller = new UserController()
        controller.create()
        
        assert controller.modelAndView.viewName.equals('/user/User')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.class == User
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [g.message(code: C.USER_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'User', action: 'save')
    } 
    
    void testTemplateExist() {
        def controller = new UserController()
        controller.params.name = "User"
        controller.template()
        assert controller.response.text != g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: ["User"])
    }
    
    void testTemplateNotExist() {
        def controller = new UserController()
        controller.template()
        assert controller.response.text.equals(g.message(code: C.DEFAULT_TEMPLATE_ERROR, args: [null]))
        controller.response.reset()
        
        controller.params.name = "null"
        controller.template()
    }
    
    void testSaveValidDataNoRoles() {
        def controller = new UserController()
        Map postParams = IFPP.initUserParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        ILP.initLoginData()
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            assert User.getAll().size() - ILP.getCreatedUsers() == 0
            controller.save()
            assert User.getAll().size() - ILP.getCreatedUsers() == 1
            assert controller.response.json.size() == 3
            assert controller.response.json.statusCode == SEC.OK_CODE
            assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), postParams.get("username")])
            assert controller.response.json.isUpdate == false
        }
    }
    
    void testSaveValidDataWithRules() {
        ILP.initLoginData()
        
        def controller = new UserController()
        Map postParams = IFPP.initUserParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.params.put("roles", ILP.getRoles())
        
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            assert User.getAll().size() - ILP.getCreatedUsers() == 0
            controller.save()
            assert User.getAll().size() - ILP.getCreatedUsers() == 1
            assert controller.response.json.size() == 3
            assert controller.response.json.statusCode == SEC.OK_CODE
            assert controller.response.json.message == g.message(code: C.DEFAULT_CREATED_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), postParams.get("username")])
            assert controller.response.json.isUpdate == false
        }        
    }
    
    void testSaveValidParamsRoleNoExist() {
        def controller = new UserController()
        Map postParams = IFPP.initUserParams(null)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.params.put("roles", ["0"])

        controller.save()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.DEFAULT_ROLE_LABEL), 0])
    }
    
    void testSaveInvalidData() {
        def controller = new UserController()
        
        assert User.getAll().size() == 0
        controller.save()
        assert User.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testEditUserExists() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        controller.params.id = user.id
        controller.edit()
        
        assert controller.modelAndView.viewName.equals('/user/User')
        assert controller.modelAndView.model.size() == 4
        assert controller.modelAndView.model.instance.equals(user)
        assert controller.modelAndView.model.formTitel == g.message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [g.message(code: C.USER_LABEL_CODE)])
        assert controller.modelAndView.model.buttonTitle == g.message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE)
        assert controller.modelAndView.model.url == g.createLink(controller:'User', action: 'update')        
    }
    
    void testEditUserExistsNot() {
        def controller = new UserController()
        assert User.getAll().size() == 0
            
        controller.edit()
        assert User.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), null])
        controller.response.reset()
        
        controller.params.id = 0
        controller.edit()
        assert User.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), 0])
    }    
    
    void testUpdateValidParamsNoRoles() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        Map postParams = IFPP.initUserParams(user)
        
        for (String key : postParams.keySet()) {
                controller.params.put(key, postParams.get(key))
        }

        ILP.initLoginData()        
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            controller.update()
            assert controller.response.json.size() == 4
            assert controller.response.json.isUpdate == true
            assert controller.response.json.id == user.id
            assert controller.response.json.statusCode == SEC.OK_CODE
            assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), user])
        }          
    }
    
    void testUpdateValidParamsLastSu() {
        def controller = new UserController()
        ILP.initLoginData()   
        
        Map postParams = IFPP.initUserParams(ILP.su)
        
        for (String key : postParams.keySet()) {
                controller.params.put(key, postParams.get(key))
        }
             
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            controller.update()
            assert controller.response.json.size() == 2
            assert controller.response.json.statusCode == SEC.USERMANAGEMENT_ERROR_CODE
            assert controller.response.json.message == g.message(code: C.DEFAULT_SU_UPDATED_EXCEPTION, args: [ILP.su])
        }   
    }
    
    void testUpdateValidParamsRoles() {
        def controller = new UserController()
        
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        
        ILP.initLoginData()
        
        Map postParams = IFPP.initUserParams(user)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.params.put("roles", ILP.getRoles())
        
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            controller.update()
            assert controller.response.json.size() == 4
            assert controller.response.json.id == user.id
            assert controller.response.json.statusCode == SEC.OK_CODE
            assert controller.response.json.message == g.message(code: C.DEFAULT_UPDATED_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), user])
            assert controller.response.json.isUpdate == true
        }  
    }
    
    void testUpdateValidParamsRoleNoExist() {
        def controller = new UserController()
        
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        
        Map postParams = IFPP.initUserParams(user)
        
        for (String key : postParams.keySet()) {
            controller.params.put(key, postParams.get(key))
        }
        
        controller.params.put("roles", ["0"])

        controller.update()
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.DEFAULT_ROLE_LABEL), 0])
    }
    
    void testUpdateInvalidParams() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        Map postParams = IFPP.initUserParams(user)
        
        for (String key : postParams.keySet()) {
            if(!(key.equals("username"))) {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        assert controller.response.json.message != null
    }
    
    void testUpdateVersionError() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        Map postParams = IFPP.initUserParams(user)
        
        for (String key : postParams.keySet()) {
            if(key.equals("userVersion")) {
                controller.params.put(key, "-1")
            } else {
                controller.params.put(key, postParams.get(key))
            }
        }
        
        controller.update()
        
        assert controller.response.json.size() == 3
        assert controller.response.json.statusCode == SEC.VERSION_ERROR_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
        assert controller.response.json.version == user.version
    }
    
    void testUpdateUserNotFound() {
        def controller = new UserController()
        assert User.getAll().size() == 0
            
        controller.update()
        assert User.getAll().size() == 0
        assert controller.response.json.size() == 2
        assert controller.response.json.statusCode == SEC.NOT_FOUND_CODE
        assert controller.response.json.message == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), null])
    }
    
    void testDeleteUserExist() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        User user = User.build()
        assert User.getAll().size() == 1
        
        controller.params.id = user.id
        controller.delete()
        assert controller.response.status == SEC.OK_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_DELETED_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), user])
        assert User.getAll().size() == 0
    }
    
    void testDeleteUserNotExists() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        
        controller.delete()
        assert controller.response.status == SEC.NOT_FOUND_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.USER_LABEL_CODE), null])
    }
    
    void testDeleteAvoidLastSuperuser() {
        def controller = new UserController()
        assert User.getAll().size() == 0
        ILP.initLoginData()
        User su = ILP.su;
        
        controller.params.id = su.id
        assert User.getAll().size() == ILP.getCreatedUsers()
        controller.delete()
        assert User.getAll().size() == ILP.getCreatedUsers()
        
        assert controller.response.status == SEC.USERMANAGEMENT_ERROR_CODE
        assert controller.response.text == g.message(code: C.DEFAULT_SU_EXCEPTION, args: [su])
    }
        
    void testDataTable() {
        def controller = new UserController()
        assert User.list().size() == 0
        
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
        controller.response.reset()
        
        User user = User.build()
        assert User.list().size() == 1
        controller.dataTable()
        assert controller.response.json.aaData.size() == 1
        assert controller.response.json.aaData.get(0).size() == 4
        controller.response.reset()
        
        user.delete(flush: true)
        controller.dataTable()
        assert controller.response.json.aaData.size() == 0
    }
}

