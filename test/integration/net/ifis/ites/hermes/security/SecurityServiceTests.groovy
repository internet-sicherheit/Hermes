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

import net.ifis.ites.hermes.security.*
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.InitLoginParameters as ILP
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Integration test for an security service
 * 
 * @author Andreas Sekulski
 */
class SecurityServiceTests extends GroovyTestCase {
    
    def g = new ApplicationTagLib()
    
    def securityService
    
    void testNotLogedUser() {
        StatusMessage responseJSON
        ILP.initLoginData()
        List<Role> roles = new ArrayList<Role>()
        roles.add(Role.get(ILP.rolemanagement.id))
        roles.add(Role.get(ILP.usermanagement.id))
        responseJSON = securityService.roleManagement(ILP.user, roles)
        assert responseJSON.getStatusCode() == SEC.USERMANAGEMENT_ERROR_CODE
        assert responseJSON.getMessage().equals(g.message(code: C.DEFAULT_USER_NOT_LOGED_IN_ERROR))  
    }

    void testWithoutSuRole() {
        StatusMessage responseJSON
        ILP.initLoginData()
        SpringSecurityUtils.doWithAuth(ILP.user.username) {
            List<Role> roles = new ArrayList<Role>()
        
            roles.add(Role.get(ILP.rolemanagement.id))
            roles.add(Role.get(ILP.usermanagement.id))
            responseJSON = securityService.roleManagement(ILP.user, roles)
            
            assert UserRole.get(ILP.user.id, ILP.superuser.id) == null

            roles.clear()
            responseJSON = securityService.roleManagement(ILP.user, roles)
            
            assert UserRole.get(ILP.user.id, ILP.superuser.id) == null
        } 
    }
    
    void testWithSuRole() {
        StatusMessage responseJSON
        ILP.initLoginData()
        SpringSecurityUtils.doWithAuth(ILP.su.username) {
            List<Role> roles = new ArrayList<Role>()
        
            roles.add(Role.get(ILP.rolemanagement.id))
            roles.add(Role.get(ILP.usermanagement.id))
            responseJSON = securityService.roleManagement(ILP.user, roles)
            
            assert responseJSON == null
            assert UserRole.get(ILP.user.id, ILP.rolemanagement.id) != null
            assert UserRole.get(ILP.user.id, ILP.usermanagement.id) != null

            roles.clear()
            responseJSON = securityService.roleManagement(ILP.user, roles)
            
            assert responseJSON == null
            assert UserRole.get(ILP.user.id, ILP.rolemanagement.id) == null
            assert UserRole.get(ILP.user.id, ILP.usermanagement.id) == null
        } 
    }
}