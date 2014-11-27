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
 * A operating system represents a concrete real world operating system.
 *
 * @author Kevin Wittek
 */
class OperatingSystem {

    /** The name of the operating system. */
    String name

    /** A general description. */
    String description

    /** Meta information about the operating system, e.g. build number. */
    String meta

    /** The type of the operating system, e.g. Unix or Win. */
    String type

    static constraints = {
        name blank: false
        description nullable: true
        meta nullable: true
        type nullable: true
    }
    
    @Override 
    public String toString() {
        return name
    }
}
