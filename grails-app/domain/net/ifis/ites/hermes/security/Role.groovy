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

import java.util.List
import java.util.ArrayList

/**
 * Represents a role that can be used for administration users.
 *
 * @author Andreas Sekulski
 */
class Role {

    String authority
    
    static mapping = {
        cache true
    }
   
    static constraints = {
        authority blank: false, unique: true
    }
    
    static Role getSuperuserRole() {
        return Role.findByAuthority("ROLE_SUPERUSER")
    }
    
    public String getMessageCode() {
        String messageCode = authority.replace('_','.').toLowerCase() ;
        return messageCode + ".label";
    }
    
    @Override 
    public String toString() {
        return authority
    }
    
    @Override
    public int hashCode() {
        return id.hashCode()
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false
        if (!(other instanceof Role)) return false
        if (id != other.id) return false
        if (!authority.equals(other.authority)) return false
        return true
    }
}