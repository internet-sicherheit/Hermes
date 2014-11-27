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

import org.springframework.mock.web.MockMultipartFile
import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Init class for post data from the CRUD formulars.
 *
 * @author asekulsk
 */
final class InitFormPostParameter {
    
    public static int ASSERT_INDEX = 0
    public static final String ASSERT_STRING_BOOLEAN_VALUE = 'true'
    public static final String ASSERT_NUMBER_VALUE = "1"
    public static final int ASSERT_INT_VALUE = 1
    public static final String ASSERT_STRING_VALUE = "Buhu"
    public static String ASSERT_DATE_TIME_STRING = "05.05.2013 17:07:00"
    public static String ASSERT_TIME_STRING = "00:00:05"
    public static MockMultipartFile ASSERT_MOCK_FILE = new MockMultipartFile("foo", "foo.txt", "text", "foooo!".bytes)
        
    public static Map initHypervisorParams(Hypervisor hypervisor) {
        Map hypervisorParams = new HashMap(
            nameHypervisor : ASSERT_STRING_VALUE,
            nothing : ASSERT_STRING_VALUE)
        
        if(hypervisor) {
            hypervisorParams.put('hypervisorID', hypervisor.id + '')
            hypervisorParams.put('hypervisorVersion', hypervisor.version + '')
        }
        
        return hypervisorParams
    }
    
    public static Map initOSParams(OperatingSystem os) {
        Map osParams = new HashMap(
            osName : ASSERT_STRING_VALUE,
            osDescription : ASSERT_STRING_VALUE,
            osType : ASSERT_STRING_VALUE,
            osMeta : ASSERT_STRING_VALUE,
            nothing : ASSERT_STRING_VALUE)
        
        if(os) {
            osParams.put('operatingSystemID', os.id + '')
            osParams.put('operatingSystemVersion', os.version + '')
        }
        
        return osParams
    }
   
    public static Map initSampleParams(Sample sample) {
        Map sampleParams = new HashMap(
            sampleName : ASSERT_STRING_VALUE,
            sampleDescription : ASSERT_STRING_VALUE,
            fileSample : ASSERT_MOCK_FILE,
            nothing : ASSERT_STRING_VALUE
        )
        
        if(sample) {
            sampleParams.put('sampleID', sample.id + '')
            sampleParams.put('sampleVersion', sample.version + '')
        }
        
        return sampleParams
    }
    
    public static Map initSensorParams(Sensor sensor) {
        Map sensorParams = new HashMap(
            sensorName : ASSERT_STRING_VALUE,
            sensorDescription : ASSERT_STRING_VALUE,
            sensorType : ASSERT_STRING_VALUE,
            md5        : ASSERT_STRING_VALUE,
            fileSensor : ASSERT_MOCK_FILE,
            nothing : ASSERT_STRING_VALUE
        )
        
        if(sensor) {
            sensorParams.put('sensorID', sensor.id + '')
            sensorParams.put('sensorVersion', sensor.version + '')
        }
        
        return sensorParams
    }

    public static Map initVMParams(VirtualMachine vm, Boolean isNew) {
        Map vmParams = new HashMap(
            vmName : ASSERT_STRING_VALUE,
            vmDescription : ASSERT_STRING_VALUE,
            fileVM : ASSERT_MOCK_FILE 
        )
        
        if(vm) {
            vmParams.put('vmID', vm.id + '')
            vmParams.put('vmVersion', vm.version + '')
        }
        
        if(isNew) {
            vmParams.osName = ASSERT_STRING_VALUE
            vmParams.osDescription = ASSERT_STRING_VALUE
            vmParams.osType = ASSERT_STRING_VALUE
            vmParams.osMeta = ASSERT_STRING_VALUE
            
            vmParams.nameHypervisor = ASSERT_STRING_VALUE
        } else {      
            Hypervisor hypervisor = Hypervisor.build()
            OperatingSystem os = OperatingSystem.build()
            
            vmParams.hypervisor = hypervisor   
            vmParams.hypervisor.id = hypervisor.id   
            vmParams.os = os   
            vmParams.os.id = os.id   
        }
        
        return vmParams
    }
    
    public static Map initJobParams(Job job) {
        Sample sample = Sample.build()
        VirtualMachine vm = VirtualMachine.build()
        Sensor sensor = Sensor.build()
        
        Map jobParams = new HashMap(
            jobChooseDate : ASSERT_DATE_TIME_STRING,
            jobEarlyStartDate : ASSERT_DATE_TIME_STRING,
            jobTimeout : ASSERT_TIME_STRING,
            jobPriority : ASSERT_NUMBER_VALUE,
            jobMemoryDump : "false",
            "sample-multi" : [sample.id],
            "sensor-multi" : [sensor.id],
            "vm-hypervisor-multi" : [vm.id]
        )
        
        if(job) {
            jobParams.put('jobID', job.id + '')
            jobParams.put('jobVersion', job.version + '')
        }
        
        return jobParams
    }
    
    public static Map initUserParams(User user) {
        Map userParams = new HashMap(
            username : ASSERT_STRING_VALUE + ASSERT_INDEX,
            password : ASSERT_STRING_VALUE,
            email : ASSERT_STRING_VALUE + ASSERT_INDEX + '@web.de',
            isEnabled : ASSERT_STRING_BOOLEAN_VALUE,
            roles : [],
            nothing : ASSERT_STRING_VALUE
        )
        
        if(user) {
            userParams.put('userID', user.id + '')
            userParams.put('userVersion', user.version + '')
        }
        
        ASSERT_INDEX++
        
        return userParams
    }
}