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

package net.ifis.ites.hermes.security

import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import net.ifis.ites.hermes.security.UserRole
import grails.converters.JSON
import org.codehaus.groovy.grails.web.pages.discovery.GrailsConventionGroovyPageLocator

/**
 * Handles incoming user data and processing the data for an user entity.
 * 
 * @author Andreas Sekulski
 */
class UserController {
    
    /**
     * Injected data table service
     **/
    def dataTableService
    
    /**
     * Injected factory to generate hypervisor params.
     **/
    def factoryParamsService

    /**
     * Dependency injection from administrationService service.
     **/
    def administrationService
    
    /**
     * Injected security service for Rolemanagment
     **/
    def securityService
    
    /**
     * Injected page locator from groovy to check if an template or view exists
     **/
    def GrailsConventionGroovyPageLocator groovyPageLocator
    
    /**
     * Renders an create formular from an user object.
     **/
    def create() {
        return render(view : "User", 
            model:[
                instance : new User(),
                formTitel : message(code: C.DEFAULT_CREATE_LABEL_CODE, args: [message(code: C.USER_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_CREATE_LABEL_CODE),
                url : createLink(controller:'User', action: 'save')])
    }
    
    /**
     * Renders an specific template if exists else an template not found error.
     * 
     * @param name Template name to render
     **/
    def template(String name) {
        if(!name || !groovyPageLocator.findTemplate(name)) {
            return render(message(code: C.DEFAULT_TEMPLATE_ERROR, args: [name]))  
        }
        
        return render(template : name, model: [params]) 
    }   
    
    /**
     * Request parameters and convert it to his User params and try to
     * save it.
     **/
    def save() {
        List<Role> listRoles = new ArrayList<Role>()
        StatusMessage responseJSON 
        User user = new User() 
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(User, params)
        
        // Check if all roles exists
        if(convertParams.roles && convertParams.roles.size() != 0) {
            for (roleId in convertParams.roles) {
                Role roleInstance = Role.get(roleId)
                if(!roleInstance) {
                    responseJSON = new StatusMessage()
            
                    responseJSON.setMessage(
                        g.message(
                            code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                            args: [message(code: C.DEFAULT_ROLE_LABEL), roleId]))
            
                    responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
                    return render(responseJSON.getJSON())
                }
                listRoles.add(roleInstance)
            }
        }

        responseJSON = administrationService.create(user, convertParams)
        if(responseJSON.getStatusCode() == SEC.OK_CODE && convertParams.roles?.size() != 0) {
            StatusMessage roleJSON = securityService.roleManagement(user ,listRoles)
            if(roleJSON != null) {
                administrationService.delete(user);
                return render(roleJSON.getJSON())
            } 
        }
        
        return render(responseJSON.getJSON())
    }
    
    /**
     * Generates an edit view from an user if an user exists
     *
     * @params id Identity number from hypervisor
     **/
    def edit(Long id) {
        User user
        StatusMessage responseJSON 
        
        try {
            user = administrationService.getEntity(User, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.USER_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
             
        return render(view : "User", 
            model : [
                instance : user,
                formTitel : message(code: C.DEFAULT_EDIT_LABEL_CODE, args: [message(code: C.USER_LABEL_CODE)]),
                buttonTitle : message(code: C.DEFAULT_BUTTON_EDIT_LABEL_CODE),
                url : createLink(controller:'User', action: 'update')
            ])      
    }    
    
    /**
     * Request parameters and convert it to his User params and try to
     * update an user.
     **/
    def update() {
        User user
        Role roleInstance
        List<Role> listRoles = new ArrayList<Role>()
        StatusMessage responseJSON
        
        // Convert incoming parmeters
        Map convertParams = factoryParamsService.convertParams(User, params)
        
        // Check if all roles exists
        if(convertParams.roles && convertParams.roles.size() != 0) {
            if(convertParams.roles instanceof String) {
                roleInstance = Role.get(new Long(convertParams.roles))
                
                if(!roleInstance) {
                    responseJSON = new StatusMessage()

                    responseJSON.setMessage(
                        g.message(
                            code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                            args: [message(code: C.DEFAULT_ROLE_LABEL), roleId]))

                    responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
                    return render(responseJSON.getJSON())
                }
                
                listRoles.add(roleInstance)
            } else {
                for (roleId in convertParams.roles) {
                    roleInstance = Role.get(roleId)

                    if(!roleInstance) {
                        responseJSON = new StatusMessage()

                        responseJSON.setMessage(
                            g.message(
                                code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                                args: [message(code: C.DEFAULT_ROLE_LABEL), roleId]))

                        responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
                        return render(responseJSON.getJSON())
                    }
                    listRoles.add(roleInstance)
                }
            }
        }
        
        try {
            user = administrationService.getEntity(User, convertParams.id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.USER_LABEL_CODE), convertParams.id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(responseJSON.getJSON())
        }
        
        // Checks if an user has update this user object before
        responseJSON = administrationService.checkVersion(user, convertParams.version)
        if(responseJSON) {
            return render(responseJSON.getJSON())
        }
                
        // Check if last su should be updated
        if(user.isLastSuperadmin() && !listRoles.contains(Role.getSuperuserRole())) {
            
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_SU_UPDATED_EXCEPTION, 
                    args: [user]))
            
            responseJSON.setStatusCode(SEC.USERMANAGEMENT_ERROR_CODE)
            
            return render(responseJSON.getJSON())
        }
        
        // Update user
        responseJSON = administrationService.update(user, convertParams)
        
        if(responseJSON.getStatusCode() == SEC.OK_CODE) {
            StatusMessage roleJSON = securityService.roleManagement(user, listRoles)
            if(roleJSON != null) {
                return render(roleJSON.getJSON())
            } 
        }

        return render(responseJSON.getJSON())
    }
    
    /**
     * Try to delete an existing user
     * 
     * @params id Identity number from an user to delete
     **/
    def delete(Long id) {
        User user
        StatusMessage responseJSON
        
        try {
            user = administrationService.getEntity(User, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.USER_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage()) 
        }
        
        def result = UserRole.findAllByRole(Role.find{authority == "ROLE_SUPERUSER"})
                
        // Check if last su should be deleted
        if(user.isLastSuperadmin()) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_SU_EXCEPTION, 
                    args: [user]))
            
            responseJSON.setStatusCode(SEC.USERMANAGEMENT_ERROR_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage()) 
        }
        
        UserRole.removeAll(user);
        responseJSON = administrationService.delete(user)
        
        return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage())  
    }
    
    /**
     * Shows an modal window for an deletion for an user.
     * 
     * @params id Identity number from an user
     **/
    def deleteModal(Long id) {
        StatusMessage responseJSON
        User user
        
        try {
            user = administrationService.getEntity(User, id)
        } catch(IllegalArgumentException iae) {
            responseJSON = new StatusMessage()
            
            responseJSON.setMessage(
                g.message(
                    code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                    args: [message(code: C.USER_LABEL_CODE), id]))
            
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            
            return render(status: responseJSON.getStatusCode(), text : responseJSON.getMessage()) 
        }

        return render(template : "../administration/modal", 
            model : [
                message : g.message(code: C.DEFAULT_BUTTON_DELETE_MESSAGE, args: [g.message(code: C.USER_LABEL_CODE), user.username])
            ])   
    }
    
    /**
     * Generates all users as an json data-table data.
     **/
    def dataTable() {
        HashMap aaData = new HashMap()
        List jsonUser = new ArrayList()

        for (user in User.list()) {
            jsonUser.add([
                    (g.message(code:C.DEFAULT_USERNAME_LABEL)) : user.username,
                    (g.message(code:C.DEFAULT_USER_EMAIL_LABEL)) : user.email,
                    (g.message(code:C.JS_DATA_TABLE_USER_STATUS_LABEL)) : getUserStatus(user.enabled), 
                    (g.message(code:C.JS_DATA_TABLE_ACTION_LABEL)) : 
                    generateActionButtons(user.id, user.username)])
        }
        
        aaData.put('aaData', jsonUser)
                        
        return render(aaData as JSON)
    }
    
    /**
     * Returns an initialization mapping from an user data table as an JSON.
     **/
    def initDataTable() {
        Map params = [
                "sAjaxSource": g.createLink(controller: "User", action: "dataTable"),
                "aoColumns": [
                    ["mData": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_USERNAME_LABEL)],
                    ["mData": g.message(code: C.DEFAULT_USER_EMAIL_LABEL)],
                    ["mData": g.message(code: C.JS_DATA_TABLE_USER_STATUS_LABEL)]
                ],
                "aoColumnDefs": [
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_ACTION_LABEL), "bSortable": false, "aTargets": [0]],
                    ["sTitle": g.message(code: C.DEFAULT_USERNAME_LABEL), "aTargets": [1]],
                    ["sTitle": g.message(code: C.DEFAULT_USER_EMAIL_LABEL), "aTargets": [2]],
                    ["sTitle": g.message(code: C.JS_DATA_TABLE_USER_STATUS_LABEL), "aTargets": [3]]
                ]
            ]
        params.putAll(dataTableService.getDefaultData())
        
        return render(params as JSON)
    }
    
    /**
     * Gets the current user status activated / deactivated user
     * 
     * @param enabled If the user ist activated / deactivated
     * 
     * @return the user status for the data table.
     **/
    private String getUserStatus(boolean enabled) {
        if(enabled) {
            return g.message(code: C.DEFAULT_USER_ENABLED_LABEL)
        } else {
            return g.message(code: C.DEFAULT_ACCOUNT_LOCKED_LABEL)
        }
    }
    
    /**
     * Generates an html5 code for specific action buttons in an data table.
     * 
     * @param id Identity from hypervisor object
     * @param name Hypervisor name
     * 
     * @return generated html code
     **/
    private String generateActionButtons(Long id, String name) {
        
        Map params = [
            id : id,
            url: [
                modal : g.createLink(url: [controller : 'User', action : 'deleteModal', params:[id:id]]),
                delete : g.createLink(url: [controller : 'User', action : 'delete', params:[id:id]])
            ]
        ]
        
        String deleteButton = g.htmlTag(tag : 'a', attributes : [
                "href=''",
                "onClick='\$(this).showDeleteDialog(" + (params as JSON) + ");return false;'"
            ], containsEndTag : true ,innerHtml : 'Löschen')

        String editButton = g.htmlTag(tag : 'a', attributes : [
                "href=\"\"",
                "onClick=\"openForm(true ," + id  + ");return false;\""
            ], containsEndTag : true ,innerHtml : 'Bearbeiten')     
        
        return editButton + "<br>" + deleteButton
    }   
}