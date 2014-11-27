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

package net.ifis.ites.hermes.util

import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import net.ifis.ites.hermes.util.Constants as C

/**
 * Container class to create an json file.
 *
 * @author Andreas Sekulski
 */
class StatusMessage {
	
    /**
     * An Json File
     **/
    private HashMap json = new HashMap()
        
    /**
     * Constructor to create an empty status message object
     **/
    StatusMessage() {
        json = new HashMap()
        json.put('statusCode', 0)
        json.put('message', '')
    }
    
    /**
     * Constructor to create an status message.
     * 
     * @params statusCode the servercode
     * @params message an message
     **/
    StatusMessage(int statusCode, String message) {
        json = new HashMap()
        json.put('statusCode', statusCode)
        json.put('message', message)
    }
    
    /**
     * Sets an status code
     * 
     * @params statusCode An server status code
     **/
    public void setStatusCode(int statusCode) {
        json.put('statusCode', statusCode)
    }
    
    /**
     * Sets an message
     * 
     * @params message
     **/
    public void setMessage(String message) {
        json.put('message', message)
    }
    
    /**
     * Gets an Status server code from this status message
     * 
     * @return gets an server code
     **/
    public int getStatusCode() {
        json.get('statusCode')
    }
    
    /**
     * Gets this Message
     * 
     * @return gets an message
     **/
    public String getMessage() {
        json.get('message')
    }
    
    /**
     * Appends an parameter to the json file if parameter exists the value
     * will be overwritten.
     * 
     * @params key the keyvalue from the json
     * @params value the mapping value to the key
     **/
    public void appendParameter(String key, Object value) {
        json.put(key, value)
    }
    
    /**
     * Converts the container to an json
     * 
     * @return the container to an json file.
     **/
    public JSON getJSON() {
        return json as JSON
    }    
}