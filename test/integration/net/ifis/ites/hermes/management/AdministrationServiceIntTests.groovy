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

import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.InitEntityParameters as IEP
import net.ifis.ites.hermes.InitCRUDParameters as ICP
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.InitLoginParameters as ILP
import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.security.User
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import net.ifis.ites.hermes.security.SecurityService
import net.ifis.ites.hermes.util.StatusMessage

/**
 * Integration test class for the AdministrationService.
 * 
 * @author Andreas Sekulski
 */
class AdministrationServiceIntTests extends GroovyTestCase {
    
    def g = new ApplicationTagLib()
        
    def administrationService
    
    private Map testFormularData
    private Map testClassData
    private Map testDomainParams
    private Map testDomainObjectData
    private List testFileSampleObjectData
    
    private List domainObjectDataList
    private List domainObjectList
    
    String getMessage(String messageCode) {
        return g.message(code : messageCode)
    }
    
    void init() {
        domainObjectDataList = [
            Hypervisor.build(), OperatingSystem.build(), VirtualMachine.build(),
            Sensor.build(), Sample.build(), Job.build(), User.build()
        ]
        
        domainObjectList = [Hypervisor, OperatingSystem, VirtualMachine, 
            Sensor, Sample, Job, User]
    }
    
    void testCreateJobsNoSensorsInt() {
        StatusMessage result
        Map params = IEP.initJobParams()
        params.remove("sensors")
        
        assert params.sensors == null
        assert Job.list().size() == 0
        result = administrationService.createJobs(params)
        assert result.json.statusCode == SEC.OK_CODE
        assert result.json.message == g.message(code: C.DEFAULT_ALL_DATA_CREATE)
        assert result.json.isUpdate == false
        assert Job.list().size() == 1
        checkJobSave(params, Job.list().get(0))
    }
    
    void testCreateJobsWithSensorsInt() {
        StatusMessage result
        Map params = IEP.initJobParams()
        
        assert Job.list().size() == 0
        result = administrationService.createJobs(params)
        assert result.json.statusCode == SEC.OK_CODE
        assert result.json.message == g.message(code: C.DEFAULT_ALL_DATA_CREATE)
        assert result.json.isUpdate == false
        assert Job.list().size() == 1
        checkJobSave(params, Job.list().get(0))
    }
    
    void testGetVMSFromHypervisorInt() {
        Map result
        List vms
        
        assert Hypervisor.list().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        assert Hypervisor.list().size() == 1
        
        assert VirtualMachine.list().size() == 0
        VirtualMachine vm = VirtualMachine.build(hypervisor : hypervisor)
        assert VirtualMachine.list().size() == 1
        
        result = administrationService.getVMSFromHypervisor()
        
        vms = result.get(hypervisor)
               
        assert vms != null
        assert vms.size() == 1
        assert vms.get(0).equals(vm)
    }
    
    void testGetEmptyListDataInt() {
        assert administrationService.getListData(Hypervisor, null).size() == 0
    }
    
    void testGetListDataInt() {
        assert Hypervisor.list().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        Hypervisor.build()
        Hypervisor.build()
        Hypervisor.build()
        assert Hypervisor.list().size() == 4
        assert administrationService.getListData(Hypervisor, [hypervisor.id]).size() == 1
    }
    
    void testGetInvalidListDataInt() {
        assert Hypervisor.list().size() == 0
        Hypervisor hypervisor = Hypervisor.build()
        Hypervisor.build()
        Hypervisor.build()
        Hypervisor.build()
        assert Hypervisor.list().size() == 4
        assert administrationService.getListData(Hypervisor, [0, hypervisor.id]).size() == 2
    }
    
    void testGetErrorsInt() {
        String result
        List errors = new ArrayList()
        errors.add("This")
        errors.add("is")
        errors.add("an")
        errors.add(Hypervisor.build())
        errors.add("error")
        result = administrationService.getErrors(errors)
        assert result.equals("This<br>is<br>an<br>error<br>")
    }
    
    void testContainsErrorsFalseInt() {
        List data = new ArrayList()
        data.add(Hypervisor.build())
        data.add(Hypervisor.build())
        data.add(Hypervisor.build())
        data.add(Hypervisor.build())
        data.add(VirtualMachine.build())
        assert administrationService.containsErrors(data) == false
    }
    
    void testContainsErrorsTrueInt() {
        List data = new ArrayList()
        data.add(Hypervisor.build())
        data.add(Hypervisor.build())
        data.add(Hypervisor.build())
        data.add("This contains an error")
        data.add(VirtualMachine.build())
        assert administrationService.containsErrors(data) == true
    }
    
    void testCheckVersionAllDomainsOK() {
        StatusMessage result      
        for(domainObject in domainObjectDataList) {
            result = administrationService.checkVersion(domainObject, domainObject.version)
            assert result == null
        }  
    }
    
    void testCheckVersionAllDomainsInvalid() {
        StatusMessage result
                
        for(domainObject in domainObjectDataList) {
            result = administrationService.checkVersion(domainObject, -1)
            assert result != null
            assert result.json.statusCode == SEC.VERSION_ERROR_CODE
            assert result.json.message == g.message(code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE)
            assert result.json.version == domainObject.version
        }
    }
            
    void testCreateValidDomains() {
        StatusMessage result
        
        Hypervisor hypervisor = new Hypervisor()
        result = administrationService.create(hypervisor, ICP.initHypervisorParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        OperatingSystem os = new OperatingSystem()
        result = administrationService.create(os, ICP.initOSParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        VirtualMachine vm = new VirtualMachine()
        result = administrationService.create(vm, ICP.initVMParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Sensor sensor = new Sensor()
        result = administrationService.create(sensor, ICP.initSensorParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Sample sample = new Sample()
        result = administrationService.create(sample, ICP.initSampleParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Job job = new Job()
        result = administrationService.create(job, ICP.initJobParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        User user = new User()
        result = administrationService.create(user, ICP.initUserParams())
        assert result.json.statusCode == SEC.OK_CODE
    }
    
    void testCreateInvalidDomains() {
        StatusMessage result
        
        Hypervisor hypervisor = new Hypervisor()
        Map params = ICP.initHypervisorParams()
        params.name = ""
        result = administrationService.create(hypervisor, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        OperatingSystem os = new OperatingSystem()
        params = ICP.initOSParams()
        params.name = ""
        result = administrationService.create(os, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        VirtualMachine vm = new VirtualMachine()
        params = ICP.initVMParams()
        params.name = ""
        result = administrationService.create(vm, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        Sensor sensor = new Sensor()
        params = ICP.initSensorParams()
        params.name = ""
        result = administrationService.create(sensor, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        Sample sample = new Sample()
        params = ICP.initSampleParams()
        params.name = ""
        result = administrationService.create(sample, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        Job job = new Job()
        params = ICP.initJobParams()
        params.name = ""
        result = administrationService.create(job, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
        
        User user = new User()
        params = ICP.initUserParams()
        params.username = ""
        result = administrationService.create(user, params)
        assert result.json.statusCode == SEC.VALIDATION_EXCEPTION_CODE
    }
    
    void testCreateNotSupportedDomain() {
        StatusMessage result
        String test = "Hello World"
        result = administrationService.create(test, ICP.initUserParams())
        assert result.json.statusCode == SEC.NOT_SUPPORTED_CODE
    }
    
    void testUpdateValidDomains() {
        StatusMessage result
        
        Hypervisor hypervisor = Hypervisor.build()
        result = administrationService.update(hypervisor, ICP.initHypervisorParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        OperatingSystem os = OperatingSystem.build()
        result = administrationService.update(os, ICP.initOSParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        VirtualMachine vm = VirtualMachine.build()
        result = administrationService.update(vm, ICP.initVMParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Sensor sensor = Sensor.build()
        result = administrationService.update(sensor, ICP.initSensorParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Sample sample = Sample.build()
        result = administrationService.update(sample, ICP.initSampleParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        Job job = Job.build()
        result = administrationService.update(job, ICP.initJobParams())
        assert result.json.statusCode == SEC.OK_CODE
        
        User user = User.build()
        result = administrationService.update(user, ICP.initUserParams())
        assert result.json.statusCode == SEC.OK_CODE
    }

    void testUpdateNotSupportedDomain() {
        StatusMessage result
        String test = "Hello World"
        result = administrationService.update(test, ICP.initUserParams())
        assert result.json.statusCode == SEC.NOT_SUPPORTED_CODE
    }
        
    void testDelete() {
        StatusMessage result
        int size
        
        Hypervisor hypervisor = Hypervisor.build()
        
        size = Hypervisor.getAll().size()
        result = administrationService.delete(hypervisor)
        assert result.json.statusCode == SEC.OK_CODE
        assert Hypervisor.getAll().size == size - 1
        
        OperatingSystem os = OperatingSystem.build()
        size =  OperatingSystem.getAll().size()
        result = administrationService.delete(os)
        assert result.json.statusCode == SEC.OK_CODE
        assert OperatingSystem.getAll().size == size - 1
        
        VirtualMachine vm = VirtualMachine.build()
        size =  VirtualMachine.getAll().size()
        result = administrationService.delete(vm)
        assert result.json.statusCode == SEC.OK_CODE
        assert VirtualMachine.getAll().size == size - 1
        
        Sensor sensor = Sensor.build()
        size =  Sensor.getAll().size()
        result = administrationService.delete(sensor)
        assert result.json.statusCode == SEC.OK_CODE
        assert Sensor.getAll().size == size - 1
        
        Sample sample = Sample.build()
        size =  Sample.getAll().size()
        result = administrationService.delete(sample)
        assert result.json.statusCode == SEC.OK_CODE
        assert Sample.getAll().size == size - 1
        
        Job job = Job.build()
        size =  Job.getAll().size()
        result = administrationService.delete(job)
        assert result.json.statusCode == SEC.OK_CODE
        assert Job.getAll().size == size - 1
        
        User user = User.build()
        size =  User.getAll().size()
        result = administrationService.delete(user)
        assert result.json.statusCode == SEC.OK_CODE
        assert User.getAll().size == size - 1
    }
        
    void testDeleteNotSupportedDomain() {
        StatusMessage result
        String test = "Hello World"
        result = administrationService.delete(test)
        assert result.json.statusCode == SEC.NOT_SUPPORTED_CODE
    }
    
    void testGetEntityAllDomainsOK() {
        Object entity
        init()
        for(int i = 0; i < domainObjectDataList.size(); i++) {
            entity = administrationService.getEntity(domainObjectList.get(i), domainObjectDataList.get(i).id)
            assert entity != null
        }
    }
    
    void testGetEntityAllDomainInvalidIllegalArgument() {
        init()
        for(int i = 0; i < domainObjectDataList.size(); i++) {
            shouldFail(IllegalArgumentException) { 
                administrationService.getEntity(domainObjectList.get(i),0)
            } 
        }
    }
    
    void testGetEntityNoDomainSupported() {
        shouldFail(IllegalArgumentException) { 
            administrationService.getEntity(null, 0)
        } 
        
        shouldFail(IllegalArgumentException) { 
            administrationService.getEntity(String, 0)
        } 
    }
    
    private void checkJobSave(Map params, Job job) {
        assert job.name != null
        assert job.sample != null
        assert job.virtualMachine != null
        assert job.simulatedTime != null
        assert job.timeout != null
        assert job.startTime != null
        assert job.priority != null
        assert job.memoryDump != null
    }
}