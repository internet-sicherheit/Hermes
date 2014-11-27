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

package net.ifis.ites.hermes

import net.ifis.ites.hermes.security.User

/**
 * Generator class to make random data
 *
 * @author Andreas Sekulski
 */
final class Generator {
    
    /** Random String size to generate **/
    private static int STRING_SIZE = 10
    
    /** Minimum range of valid ASCII characters (65 --> A) **/
    private static int MIN_ASCII = 65
    
    /** Maximum range of valid ASCII characters (90 --> Z) **/
    private static int MAX_ASCII = 90
    
    /**
     * Generates an random user
     * 
     * @return An custom generated User
     **/
    public static User generateUser() {
        String randomString = randomString(STRING_SIZE)
        
        String username = randomString
        String email = randomString + "@web.de"
        
        return User.build(username : username, email : email)
    }
    
    /**
     * Generates an random String
     * 
     * @param size String size to generate
     * 
     * @return An custom generated String
     **/
    private static String randomString(int size) {
        String random = ""
        
        // Random ASCII Char from 65 (A) to 90 (Z)
        for(int i = 0; i < size; i++) {
            random += (char)(randomInt(MIN_ASCII, MAX_ASCII))
        }
        
        return random
    }
    
    /**
     * Generates an custom Integer value from an given range
     * 
     * @params min Minimum range value
     * @params max Maximum range value
     * @return custom Integer value from min and max range
     **/
    private static int randomInt(int min, int max) {
        Random rand = new Random()
        int range = max - min - 1
        int randomNum = rand.nextInt(range) + min
        return randomNum;
    }	
}