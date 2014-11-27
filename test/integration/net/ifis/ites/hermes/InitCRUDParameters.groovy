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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ifis.ites.hermes

import java.sql.Time
import java.util.Date
import net.ifis.ites.hermes.management.*
import org.springframework.mock.web.MockMultipartFile
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.InitEntityParameters as IEP
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import net.ifis.ites.hermes.security.User

/**
 *
 * @author Andreas
 */
class InitCRUDParameters {

    public static final String ASSERT_STRING_VALUE = "Schuhu"
    public static final int ASSERT_INT_VALUE = 1
    public static final boolean ASSERT_BOOLEAN_VALUE = true
    public static final Time ASSERT_TIME_VALUE = new Time(0,5,0)
    public static final Date ASSERT_DATE_VALUE = new Date()
    public static def generateMockFile = { new MockMultipartFile("foo", "foo.txt", "text", Generator.randomString(5).bytes) }

    public static Map initHypervisorParams(Hypervisor hypervisor) {
        if(hypervisor != null) {
            return new HashMap(
                id : hypervisor.id,
                version : hypervisor.version,
                name : ASSERT_STRING_VALUE
            )
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE
            )
        }
    }

    public static Map initOSParams(OperatingSystem os) {
        if(os != null) {
            return new HashMap(
                id : os.id,
                version : os.version,
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                type : ASSERT_STRING_VALUE,
                meta : ASSERT_STRING_VALUE
            )
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                type : ASSERT_STRING_VALUE,
                meta : ASSERT_STRING_VALUE
            )
        }
    }

    public static Map initSampleParams(Sample sample) {
        if(sample != null) {
            return new HashMap(
                id : sample.id,
                version : sample.version,
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                file : generateMockFile()
            )
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                file : generateMockFile()
            )
        }
    }

    public static Map initSensorParams(Sensor sensor) {
        if(sensor != null) {
            return new HashMap(
                id : sensor.id,
                version : sensor.version,
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                type : ASSERT_STRING_VALUE,
                file : generateMockFile()
            )
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                type : ASSERT_STRING_VALUE,
                file : generateMockFile()
            )
        }
    }

    public static Map initVMParams(VirtualMachine vm) {
        if(vm != null) {
            return new HashMap(
                id : vm.id,
                version : vm.version,
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                file : generateMockFile(),
                operatingSystemInstance : OperatingSystem.build(),
                hypervisorInstance : Hypervisor.build(),
                osParams : null,
                hypervisorParams : null
            )
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE,
                description : ASSERT_STRING_VALUE,
                file : generateMockFile(),
                operatingSystemInstance : null,
                hypervisorInstance : null,
                osParams : initOSParams(null),
                hypervisorParams : initHypervisorParams(null)
            ) 
        }
    }
    
    public static Map initJobParams(Job job) {
        if(job != null) {
            return new HashMap(
                id : job.id,
                version : job.version,
                name : ASSERT_STRING_VALUE,
                simulatedTime : ASSERT_DATE_VALUE,
                timeout : ASSERT_TIME_VALUE,
                priority : ASSERT_INT_VALUE,
                memoryDump : ASSERT_BOOLEAN_VALUE,
                available : ASSERT_BOOLEAN_VALUE,
                vm : VirtualMachine.build(),
                sensorInstance : Sensor.build(),
                sampleInstance : Sample.build(),
                sensorParams : null,
                sampleParams : null
            ) 
        } else {
            return new HashMap(
                name : ASSERT_STRING_VALUE,
                simulatedTime : ASSERT_DATE_VALUE,
                timeout : ASSERT_TIME_VALUE,
                priority : ASSERT_INT_VALUE,
                memoryDump : ASSERT_BOOLEAN_VALUE,
                available : ASSERT_BOOLEAN_VALUE,
                vm : VirtualMachine.build(),
                sensorInstance : null,
                sampleInstance : null,
                sensorParams : initSensorParams(null),
                sampleParams : initSampleParams(null)
            ) 
        }
    }
    
    public static Map initUserParams(User user) {
        if(user != null) {
            return new HashMap(
                id : user.id,
                version : user.version,
                username : ASSERT_STRING_VALUE,
                password : ASSERT_STRING_VALUE,
                email : ASSERT_STRING_VALUE,
                enabled : ASSERT_BOOLEAN_VALUE,
                accountExpired : ASSERT_BOOLEAN_VALUE,
                accountLocked : ASSERT_BOOLEAN_VALUE,
                passwordExpired : ASSERT_BOOLEAN_VALUE,
                roles : []
            ) 
        } else {
            return new HashMap(
                username : ASSERT_STRING_VALUE,
                password : ASSERT_STRING_VALUE,
                email : ASSERT_STRING_VALUE + '@web.de',
                enabled : ASSERT_BOOLEAN_VALUE,
                accountExpired : ASSERT_BOOLEAN_VALUE,
                accountLocked : ASSERT_BOOLEAN_VALUE,
                passwordExpired : ASSERT_BOOLEAN_VALUE,
                roles : []
            ) 
        }
    }
}