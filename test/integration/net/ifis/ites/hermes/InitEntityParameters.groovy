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

import java.sql.Time
import java.util.Date
import net.ifis.ites.hermes.management.*

/**
 * 
 * Init class for Entity Paramaeters to save it in an Domain Class.
 * 
 * @author Andreas Sekulski
 */
final class InitEntityParameters {
	
    public static final String ASSERT_STRING_VALUE = "Buhu"
    public static final int ASSERT_INT_VALUE = 0
    public static final boolean ASSERT_BOOLEAN_VALUE = false
    public static final Time ASSERT_TIME_VALUE = new Time(0,0,5)
    public static final Date ASSERT_DATE_VALUE = new Date()
       
    
    public static Map initHypervisorParams() {
        Map hypervisorParams = new HashMap(
            name : ASSERT_STRING_VALUE
        )
        return hypervisorParams
    }
    
    public static Map initOSParams() {
        Map osParams = new HashMap(
            name : ASSERT_STRING_VALUE,
            description : ASSERT_STRING_VALUE,
            type : ASSERT_STRING_VALUE,
            meta : ASSERT_STRING_VALUE
        )
        return osParams
    }
    
    public static Map initSampleParams() {
        Map sampleParams = new HashMap(
            name : ASSERT_STRING_VALUE,
            description : ASSERT_STRING_VALUE,
            originalFilename : ASSERT_STRING_VALUE,
            fileExtension : ASSERT_STRING_VALUE,
            fileContentType : ASSERT_STRING_VALUE,
            md5 : ASSERT_STRING_VALUE,
            sha1 : ASSERT_STRING_VALUE,
            sha256 : ASSERT_STRING_VALUE,
            sha512 : ASSERT_STRING_VALUE
        )
        return sampleParams
    }

    public static Map initSensorParams() {
        Map sensorParams = new HashMap(
            name : ASSERT_STRING_VALUE,
            description : ASSERT_STRING_VALUE,
            type : ASSERT_STRING_VALUE,
            md5 : ASSERT_STRING_VALUE,
            originalFilename : ASSERT_STRING_VALUE
        )
        return sensorParams
    }
    
    public static Map initVMParams() {
        Map vmParams = new HashMap(
            name : ASSERT_STRING_VALUE,
            description : ASSERT_STRING_VALUE,
            originalFilename : ASSERT_STRING_VALUE,
            os : OperatingSystem.build(),
            hypervisor : Hypervisor.build()
        )
        return vmParams
    }
    
    public static Map initJobParams() {
        Map jobParams = new HashMap(
            simulatedTime : ASSERT_DATE_VALUE,
            startDate : ASSERT_DATE_VALUE,
            timeout : ASSERT_TIME_VALUE,
            priority : ASSERT_INT_VALUE,
            memoryDump : ASSERT_BOOLEAN_VALUE,
            sensors : [Sensor.build()],
            samples : [Sample.build()],
            vms : [VirtualMachine.build()]
        )
        return jobParams
    }
    
    public static Map initUserParams() {
        Map userParams = new HashMap(
            username : ASSERT_STRING_VALUE,
            password : ASSERT_STRING_VALUE,
            email : ASSERT_STRING_VALUE,
            enabled : ASSERT_BOOLEAN_VALUE,
            accountExpired : ASSERT_BOOLEAN_VALUE,
            accountLocked : ASSERT_BOOLEAN_VALUE,
            passwordExpired : ASSERT_BOOLEAN_VALUE
        )
        return userParams
    }
}