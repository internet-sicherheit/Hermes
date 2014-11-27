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

import net.ifis.ites.hermes.util.StatusMessage
import org.springframework.dao.DataIntegrityViolationException
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.Constants as C
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Security service to manage all user management.
 *
 * @author Andreas Sekulski
 */
class SecurityService {
    
    /**
     * Injected TagLib Service
     **/
    def g = new ApplicationTagLib()
    
    /**
     * Injected spring security service
     **/
    def springSecurityService
    
    /**
     * Update set from updated user
     **/
    final Set<String> userUpdates = new HashSet<String>()

    /**
     * Role management method to append or delete roles from an user if loged
     * user has rights to change it.
     * 
     * @param userInstance instance from user
     * @param listRoles from rolse how should be removed or append
     * 
     * @return an StatusMessage if changes are completed null will be returned
     * or an error status message
     **/
    public StatusMessage roleManagement(User userInstance, List<Role> listRoles) {
        Boolean hasAccess
        StatusMessage responseJSON = new StatusMessage()
        User logedUser = springSecurityService.currentUser
                
        if(!logedUser) {
            responseJSON.setStatusCode(SEC.USERMANAGEMENT_ERROR_CODE)
            responseJSON.setMessage(g.message(code: C.DEFAULT_USER_NOT_LOGED_IN_ERROR))
            return responseJSON
        }
        
        hasAccess = logedUser.containsRole(Role.findByAuthority("ROLE_SUPERUSER"))
        
        if(!listRoles.empty && hasAccess) {
            for (Role role in Role.getAll()) {
                if(userInstance.containsRole(role) && !listRoles.contains(role)) {
                    UserRole.remove(userInstance, role, true)
                } else if(!userInstance.containsRole(role) && listRoles.contains(role)) {
                    UserRole.create(userInstance, role, true)
                }
            }
        } else if(listRoles.empty && hasAccess) {
            UserRole.removeAll(userInstance)         
        }

        markUserUpdate(userInstance.username)
                
        return null;
    }
    
    /**
     * Marks an given user to update session
     * 
     * @param username Name from given user to update
     **/
    public void markUserUpdate(String username) {
        synchronized (userUpdates) {
            userUpdates.add(username)
        }
    }

    /**
     * Clears an given user from updating the session
     * 
     * @param username Name from given user to not update
     **/
    public boolean clearUserUpdate(String username) {
        synchronized (userUpdates) {
            return userUpdates.remove(username) != null
        }
    }

    /**
     * Checks if user must update his session.
     * 
     * @return true if user must update session else false
     **/
    public boolean checkUserUpdate() {
        def principal = springSecurityService.principal

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            synchronized (userUpdates) {
                if (!userUpdates.remove(principal.username)) {
                    return true
                }
            }

            springSecurityService.reauthenticate(principal.username)

            return false
        }

        return true
    }
    
    /**
     * Checks if user is still in database if not he will be force to log out.
     * 
     * @return true if user is loged in else false
     **/
    public boolean userStillAlive() {
        User logedUser = springSecurityService.currentUser
        if(!logedUser || !logedUser.enabled) {
            SecurityContextHolder.clearContext()
            return false
        }
        return true
    }
}