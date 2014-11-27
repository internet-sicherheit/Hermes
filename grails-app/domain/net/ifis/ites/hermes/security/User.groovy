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

import net.ifis.ites.hermes.security.Role

/**
 * Represents a user.
 *
 * @author Andreas Sekulski
 */
class User {

    transient springSecurityService

    String username
    String password
    String email
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    
    static constraints = {
        username blank: false, unique: true
        email blank:false, unique: true
        password blank: false
    }

    static mapping = {
        table '`user`'
        password column: '`password`'
    }

    List<Role> getAuthorities() {
        return UserRole.findAllByUser(this).collect{it.role} as List
    }
    
    boolean containsRole(Role role) {
        return role && UserRole.get(id, role.id) != null
    }
    
    boolean hasRoles() {
        return getAuthorities().size() > 0
    }
    
    boolean isSuperadmin() {
        return getAuthorities().contains(Role.getSuperuserRole())
    }
    
    boolean isLastSuperadmin() {
        def admins = UserRole.findAllByRole(Role.getSuperuserRole())
        return (admins.size() == 1 && admins.get(0).user.equals(this))
    }
    
    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }
    
    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }
        
    @Override 
    public String toString() {
        return username
    }
    
    @Override
    public int hashCode() {
        return id.hashCode()
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false
        if (!(other instanceof User)) return false
        if (id != other.id) return false
        if (!username.equals(other.username)) return false
        if (!email.equals(other.email)) return false
        return true
    }    
}