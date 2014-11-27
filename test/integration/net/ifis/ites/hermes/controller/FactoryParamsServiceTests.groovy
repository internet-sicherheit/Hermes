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

package net.ifis.ites.hermes.controller

import java.text.SimpleDateFormat
import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.InitFormPostParameter as IFPP
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Testclass for the Factory Params Service how converts incoming post data
 * from the formular for the maping domain class.
 * 
 * @author Andreas Sekulski
 */
class FactoryParamsServiceTests extends GroovyTestCase {
    
    def g = new ApplicationTagLib()
    
    def factoryParamsService
    
    private String getMessage(String messageCode) {
        return g.message(code : messageCode)
    }
       
    void testHypervisorFactoryService() {
        Hypervisor hypervisor = Hypervisor.build()
        Map convertParams = factoryParamsService.convertParams(Hypervisor, IFPP.initHypervisorParams(null))
        checkHypervisor(convertParams, null)
        convertParams = factoryParamsService.convertParams(Hypervisor, IFPP.initHypervisorParams(hypervisor))
        checkHypervisor(convertParams, hypervisor)
    }
    
    void checkHypervisor(Map convertParams, Hypervisor hypervisor) {
        assert convertParams.name == IFPP.ASSERT_STRING_VALUE
        assert convertParams.nothing == null
        
        if(hypervisor) {
            assert convertParams.id == hypervisor.id
            assert convertParams.version == hypervisor.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testOSFactoryService() {
        OperatingSystem os = OperatingSystem.build()
        Map convertParams = factoryParamsService.convertParams(OperatingSystem, IFPP.initOSParams(null))
        checkOS(convertParams, null)
        convertParams = factoryParamsService.convertParams(OperatingSystem, IFPP.initOSParams(os))
        checkOS(convertParams, os)
    }
    
    void checkOS(Map convertParams, OperatingSystem os) {
        assert convertParams.name == IFPP.ASSERT_STRING_VALUE
        assert convertParams.description == IFPP.ASSERT_STRING_VALUE
        assert convertParams.type == IFPP.ASSERT_STRING_VALUE
        assert convertParams.meta == IFPP.ASSERT_STRING_VALUE
        assert convertParams.nothing == null
        
        if(os) {
            assert convertParams.id == os.id
            assert convertParams.version == os.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testSampleFactoryService() {
        Sample sample = Sample.build()
        Map convertParams = factoryParamsService.convertParams(Sample, IFPP.initSampleParams(null))
        checkSample(convertParams, null)
        convertParams = factoryParamsService.convertParams(Sample, IFPP.initSampleParams(sample))
        checkSample(convertParams, sample)
    }
    
    void checkSample(Map convertParams, Sample sample) {
        assert convertParams.name == IFPP.ASSERT_STRING_VALUE
        assert convertParams.description == IFPP.ASSERT_STRING_VALUE
        assert convertParams.file == IFPP.ASSERT_MOCK_FILE
        assert convertParams.nothing == null
        
        if(sample) {
            assert convertParams.id == sample.id
            assert convertParams.version == sample.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testSensorFactoryService() {
        Sensor sensor = Sensor.build()
        Map convertParams = factoryParamsService.convertParams(Sensor, IFPP.initSensorParams(null))
        checkSensor(convertParams, null)
        convertParams = factoryParamsService.convertParams(Sensor, IFPP.initSensorParams(sensor))
        checkSensor(convertParams, sensor)
    }
    
    void checkSensor(Map convertParams, Sensor sensor) {
        assert convertParams.name == IFPP.ASSERT_STRING_VALUE
        assert convertParams.description == IFPP.ASSERT_STRING_VALUE
        assert convertParams.type == IFPP.ASSERT_STRING_VALUE
        assert convertParams.file == IFPP.ASSERT_MOCK_FILE
        assert convertParams.nothing == null
        
        if(sensor) {
            assert convertParams.id == sensor.id
            assert convertParams.version == sensor.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testVMFactoryService() {
        VirtualMachine vm = VirtualMachine.build()
        Map convertParams
        
        //TestCase OperatingSystem and Hypervisor is new
        convertParams = factoryParamsService.convertParams(VirtualMachine, IFPP.initVMParams(null ,true))
        
        checkVM(convertParams, null)
        checkOS(convertParams.osParams, null)
        assert convertParams.operatingSystemInstance == null
        checkHypervisor(convertParams.hypervisorParams, null)
        assert convertParams.hypervisorInstance == null
        
        convertParams = factoryParamsService.convertParams(VirtualMachine, IFPP.initVMParams(vm ,true))
        checkVM(convertParams, vm)
        checkOS(convertParams.osParams, null)
        assert convertParams.operatingSystemInstance == null
        checkHypervisor(convertParams.hypervisorParams, null)
        assert convertParams.hypervisorInstance == null
                
        //TestCase OperatingSystem and Hypervisor is not new
        convertParams = factoryParamsService.convertParams(VirtualMachine, IFPP.initVMParams(null ,false))
        
        checkVM(convertParams, null)
        assert convertParams.osParams == null
        assert convertParams.operatingSystemInstance != null
        
        assert convertParams.hypervisorParams == null
        assert convertParams.hypervisorInstance != null
        
        convertParams = factoryParamsService.convertParams(VirtualMachine, IFPP.initVMParams(vm ,false))
        
        checkVM(convertParams, vm)
        assert convertParams.osParams == null
        assert convertParams.operatingSystemInstance != null
        
        assert convertParams.hypervisorParams == null
        assert convertParams.hypervisorInstance != null
    }
    
    void checkVM(Map convertParams, VirtualMachine vm) {
        assert convertParams.name == IFPP.ASSERT_STRING_VALUE
        assert convertParams.description == IFPP.ASSERT_STRING_VALUE
        assert convertParams.file == IFPP.ASSERT_MOCK_FILE
        assert convertParams.nothing == null
        
        if(vm) {
            assert convertParams.id == vm.id
            assert convertParams.version == vm.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testJobFactoryService() {
        Job job = Job.build()
        Map convertParams
        convertParams = factoryParamsService.convertParams(Job, IFPP.initJobParams(null))
        checkJob(convertParams, null)
    }    
    
    void checkJob(Map convertParams, Job job) {
        assert convertParams.simulatedTime == new SimpleDateFormat(
            getMessage(C.DEFAULT_DATE_TIME_FORMAT_CODE), 
            Locale.GERMANY).parse(IFPP.ASSERT_DATE_TIME_STRING)
        assert convertParams.startDate == new SimpleDateFormat(
            getMessage(C.DEFAULT_DATE_TIME_FORMAT_CODE), 
            Locale.GERMANY).parse(IFPP.ASSERT_DATE_TIME_STRING)
        assert convertParams.timeout == new SimpleDateFormat(
            getMessage(C.DEFAULT_TIME_FORMAT_CODE),
            Locale.GERMANY).parse(IFPP.ASSERT_TIME_STRING)
        assert convertParams.priority == IFPP.ASSERT_INT_VALUE
        assert convertParams.samples.size() == 1
        assert convertParams.sensors.size() == 1
        assert convertParams.vms.size() == 1
        assert convertParams.nothing == null
        
        if(job) {
            assert convertParams.id == job.id
            assert convertParams.version == job.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testUserFactoryService() {
        User user = User.build()
        Map convertParams = factoryParamsService.convertParams(User, IFPP.initUserParams(null))
        checkUser(convertParams, null)
        convertParams = factoryParamsService.convertParams(User, IFPP.initUserParams(user))
        checkUser(convertParams, user)
    }
    
    void checkUser(Map convertParams, User user) {
        assert convertParams.username.contains(IFPP.ASSERT_STRING_VALUE) == true
        assert convertParams.password == IFPP.ASSERT_STRING_VALUE
        assert convertParams.email.contains(IFPP.ASSERT_STRING_VALUE) == true
        assert convertParams.enabled == true
        assert convertParams.accountLocked == false
        assert convertParams.roles.size() == 0
        assert convertParams.nothing == null
        
        if(user) {
            assert convertParams.id == user.id
            assert convertParams.version == user.version
        } else {
            assert convertParams.id == null
            assert convertParams.version == null
        }
    }
    
    void testUnsupportedExceptionService() {
        try { 
            factoryParamsService.convertParams(null, null)
            fail("Unsupported OperationException not thrown") 
        } catch(UnsupportedOperationException ex) { 
            String assertMessage = getMessage(C.DEFAULT_NOT_SUPPORTED_ERROR)
            assert assertMessage.equals(ex.getMessage())
        } 
    }  
}