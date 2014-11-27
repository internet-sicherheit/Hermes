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



import grails.test.mixin.*
import net.ifis.ites.hermes.InitMockException
import org.apache.commons.io.FileUtils
import org.junit.*
import org.springframework.mock.web.MockMultipartFile

import java.sql.Time

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(ManagementService)
@Mock([VirtualMachine, Hypervisor, OperatingSystem, Sensor, Sample, Job])
class ManagementServiceTests {

    String uploadDir = "testUpload/"

    @Before
    void setUp() {
        grailsApplication.config.hermes.dirUpload = uploadDir
    }

    @After
    void tearDown() {
        FileUtils.deleteDirectory(new File(uploadDir))
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
        vm.grailsApplication = grailsApplication
        return vm
    }

    Sensor initSensor() {
        Sensor sensor = new Sensor(
                name: "sensor",
                description: "foobar",
                type: "sensorType",
                originalFilename: "sensor.sens",
                md5: "123"
        )
        sensor.grailsApplication = grailsApplication

        sensor.save()

        if (sensor.hasErrors()) {
            throw new InitMockException(Sensor.class, "initSensor", sensor.errors)
        }

        return sensor
    }

    Sample initSample() {
        Sample sample = new Sample(
                name: "sample",
                originalFilename: "sample.exe",
                fileExtension: "exe",
                fileContentType: "application",
                description: "simple sample",
                md5: "1",
                sha1: "2",
                sha256: "3",
                sha512: "4",
                ssdeep: "5",
                crc32: "6"
        )
        sample.save()

        if (sample.hasErrors()) {
            throw new InitMockException(Sample.class, "initSample", sample.errors)
        }

        return sample
    }

    void testDeleteVirtualMachine_SingleVm_VmIsDeleted() {

        VirtualMachine vm = initVirtualMachine()
        vm.save(flush: true)

        assert VirtualMachine.exists(vm.id)

        service.deleteVirtualMachine(vm)

        assert VirtualMachine.count() == 0

    }

    void testDeleteVirtualMachine_MultipleVmWithSimilarOs_VmIsDeletedAndOsRemains() {

        OperatingSystem os = new OperatingSystem(
                name: "abc"
        )

        VirtualMachine remainingVm = initVirtualMachine()
        remainingVm.os = os

        VirtualMachine deletedVm = initVirtualMachine()
        deletedVm.os = os

        remainingVm.save()
        deletedVm.save(flush: true)

        assertTrue VirtualMachine.exists(deletedVm.id)

        service.deleteVirtualMachine(deletedVm)

        // force flush
        remainingVm.save(flush: true)

        assertFalse VirtualMachine.exists(deletedVm.id)

        assert remainingVm.os == os

    }

    void testUpdateSensor_CorrectParamsNoFile_ReturnValidUpdatedSensor() {

        Sensor s = initSensor()
        Sensor flushProxy = initSensor()

        String newName = "mightymouse"

        service.updateSensor(
                s,
                newName,
                s.type,
                s.description,
                null
        )

        assert s.validate()
        assert s.name == newName
    }

    void testUpdateSensor_WrongParamsNoFile_ReturnNotValidSensor() {
        Sensor s = initSensor()

        String newName = ""

        try {
            service.updateSensor(
                s,
                newName,
                s.type,
                s.description,
                null
            )
        } catch(ValidationException ve) {
            assert !(ve.transactionObject.validate())
        }
    }

    void testUpdateSample_CorrectParamsNoFile_ReturnValidUpdatedSample() {
        Sample sample = initSample()

        String newName = "megaman"
        service.updateSample(sample, newName, sample.description, null)

        assert sample.validate()
        assert sample.name == newName

    }

    void testUpdateSample_WrongParamsNoFile_ReturnNotValidSample() {
        Sample sample = initSample()

        String newName = ""
        try {
            service.updateSample(sample, newName, sample.description, null)
        } catch(ValidationException ve) {
            assert !(ve.transactionObject.validate())
        }
    }
}