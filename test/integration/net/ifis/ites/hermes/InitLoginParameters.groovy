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

package net.ifis.ites.hermes

import net.ifis.ites.hermes.security.Role
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.security.UserRole

/**
 *
 * @author Andreas Sekulski
 */
final class InitLoginParameters {
    
    static User user
    static User su
    static Role superuser
    static Role rolemanagement
    static Role usermanagement
    
    public static void initLoginData() {
        // Setup logic here
        createSU()
        createUser()
    }
    
    public static User createSU() {
        // Init SU Role
        superuser = getRole("ROLE_SUPERUSER")
        
        // Init user
        su = getAccount([
            username : 'su',
            password : '123456',
            email : 'su@web.de',
            enabled : true
        ])
        
        // Init associated user role
        initUserRole(su, superuser)
        
        return su
    }
    
    public static User createUser() {
        // Init user roles
        rolemanagement = getRole("ROLE_ROLEMANAGEMENT")
        usermanagement = getRole("ROLE_USERMANAGEMENT")
        
        // Init user
        user = getAccount([
            username : 'test',
            password : '123456',
            email : 'test@web.de',
            enabled : true
        ])
        
        // Init associated user role
        initUserRole(user, rolemanagement)
        initUserRole(user, usermanagement)
                
        return user
    }
    
    public static int getCreatedUsers() {
        return 2
    }
    
    public static List getRoles() {
        ArrayList list = new ArrayList()
        list.add(usermanagement.id)
        list.add(rolemanagement.id)
        return list
    }
    
    public static Role getRole(String roleName) {
        Role role = Role.findByAuthority(roleName)
        if(!role) {
            role = new Role(authority : roleName)
            assert role.save(flush : true) != null
        }
        return role
    }
    
    private static User getAccount(Map params) {
        User user = User.findByUsername(params.username)
        if(!user) {
            user = new User()
            user.username = params.username
            user.password = params.password
            user.email = params.email
            user.enabled = params.enabled
            assert user.save(flush : true) != null
        }
        return user
    }
    
    private static void initUserRole(User user, Role role) {
        UserRole userRole = UserRole.findByUserAndRole(user, role)
        if(!userRole) {
            UserRole.create(user, role, true)
        }
    }
}