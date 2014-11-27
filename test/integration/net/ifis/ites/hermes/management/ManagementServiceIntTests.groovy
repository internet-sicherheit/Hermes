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

import org.apache.commons.io.FileUtils
import org.junit.After
import org.springframework.mock.web.MockMultipartFile

import java.sql.Time

/**
 * Transactional integration tests for ManagementService.
 *
 * @author Kevin Wittek
 */
class ManagementServiceIntTests extends GroovyTestCase {

    static transactional = false

    def managementService
    def grailsApplication

    @After
    void tearDown() {
        VirtualMachine.executeUpdate("delete VirtualMachine")
        Hypervisor.executeUpdate("delete Hypervisor")
        OperatingSystem.executeUpdate("delete OperatingSystem")
        Sensor.executeUpdate("delete Sensor")

        FileUtils.deleteDirectory(new File(grailsApplication.config.hermes.dirUpload))
    }

    VirtualMachine initVirtualMachine() {
        VirtualMachine vm = new VirtualMachine(
                name: "vm",
                originalFilename: "foo.txt",
                description: "foobar",
                hypervisor: new Hypervisor(
                        name: "hyper"
                ),
                os: new OperatingSystem(
                        name: "windows",
                        description: "camelot",
                        meta: "mega",
                        type: "win32"
                )

        )
        return vm
    }

    void testCreateJob_WrongParamsNewSensorNewSample_ReturnNotValidJobSampleAndSensorNotCreated() {

        VirtualMachine vm = initVirtualMachine()
        vm.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)


        def sensorParams = [name: "sens", type: "type", description: "desc", file: mockFile]
        def sampleParams = [name: "sample", description: "foobar", file: mockFile]


        // blank name should not persist
        Job job
        try {

            job = managementService.createJob("",
                    new Date(),
                    new Time(1),
                    1,
                    true,
                    true,
                    vm,
                    null,
                    null,
                    sensorParams,
                    sampleParams)

        } catch (ValidationException e) {
            assert e
            job = e.transactionObject
        }

        Sample sample = job.sample
        Sensor sensor = job.sensor

        assert Job.count == 0
        assert !Sample.exists(sample.id)
        assert !Sensor.exists(sensor.id)

        File sampleFile = new File(sample.getFileLocation())
        assert !sampleFile.exists()

        File sensorFile = new File(sensor.getFileLocation())
        assert !sensorFile.exists()

    }


    void testCreateVm_WrongParamsNewHypervisorNewOs_ReturnNotValidVmOsAndJobNotCreated() {

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        def osParams = [
                name: "windows",
                description: "no penguin",
                meta: "megaman",
                type: "mineralwasser"
        ]

        def hyperParams = [
                name: "kvm"
        ]

        // black name should no persist
        VirtualMachine vm
        try {
            vm = managementService.createVirtualMachine("", mockFile, "desc", null, null, osParams, hyperParams)
        } catch(ValidationException ve) {
            vm = ve.transactionObject
            OperatingSystem os = vm.os
            Hypervisor hyper = vm.hypervisor

            assert vm.hasErrors()

            File imageFile = new File(vm.getFileLocation())
            assert !imageFile.exists()

            assert VirtualMachine.count == 0
            assert !OperatingSystem.exists(os.id)
            assert !Hypervisor.exists(hyper.id)
        }
    }

    void testCreateSensor_HelloWorldFile_ValidSensorCorrectHash() {

        byte[] fileContent = "hello world".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        Sensor sensor = managementService.createSensor("sensor", "rattatata", "zoppotrumpel", mockFile)

        assert sensor.validate()
        assert sensor.md5 == "5eb63bbbe01eeed093cb22bb8f5acdc3"

    }

}
