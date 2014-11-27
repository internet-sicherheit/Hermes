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

/**
 * Represents a hypervisor that can be used to run images of virtual machines.
 *
 * @author Kevin Wittek
 */
class Hypervisor {

    /** The name of the hypervisor. */
    String name

    static constraints = {
        name blank: false
    }
        
    @Override 
    public String toString() {
        return name
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Hypervisor)) return false

        Hypervisor that = (Hypervisor) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return id.hashCode()
    }
}
