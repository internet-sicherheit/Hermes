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
import org.apache.commons.io.FileUtils
import org.junit.*
import org.springframework.mock.web.MockMultipartFile

import java.nio.file.Files

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(Sensor)
class SensorTests {

    String uploadDir = "testUpload/"

    @Before
    void setUp() {
        grailsApplication.config.hermes.dirUpload = uploadDir
    }

    @After
    void tearDown() {
        FileUtils.deleteDirectory(new File(uploadDir))
    }

    Sensor initSensor() {
        Sensor sensor = new Sensor(
                name: "sensor",
                type: "sonar",
                description: "foobar",
                originalFilename: "sonar.bin",
                md5: "1234"
        )

        sensor.grailsApplication = grailsApplication
        return sensor
    }

    void testConstraints() {
        // test creating a valid sensor
        Sensor sensor = initSensor()
        assert sensor.validate()

        // test blank constraints
        sensor.name = ""
        assert !sensor.validate()
        assert 1 == sensor.errors.errorCount
        assert "blank" == sensor.errors["name"].code

        sensor = initSensor()
        sensor.originalFilename = ""
        assert !sensor.validate()
        assert 1 == sensor.errors.errorCount
        assert "blank" == sensor.errors["originalFilename"].code

        // test nullable
        sensor = initSensor()
        sensor.md5 = null
        assert !sensor.validate()
        assert 1 == sensor.errors.errorCount
        assert "nullable" == sensor.errors["md5"].code

    }

    void testGetFileLocation() {
        Sensor sensor = initSensor()
        sensor.save(flush: true)
        long id = sensor.id

        assert uploadDir + Sensor.DIR_SENSORS + id + "/sonar.bin" == sensor.getFileLocation()

    }

    void testGetDir() {
        Sensor sensor = initSensor()
        sensor.save(flush: true)
        long id = sensor.id

        assert uploadDir + Sensor.DIR_SENSORS + id + "/" == sensor.getDir()
    }

    void testWriteSampleFile() {

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "sonar.bin", "binary", fileContent)

        Sensor sensor = initSensor()
        sensor.save(flush: true)
        long id = sensor.id
        File writtenFile = sensor.writeSensorFile(mockFile)

        assert fileContent == writtenFile.bytes
        File expectedPath = new File(uploadDir + Sensor.DIR_SENSORS + id + "/sonar.bin")
        File resultPath = new File(writtenFile.getPath())

        assert expectedPath == resultPath
    }

    void testDeleteFile() {

        Sensor sensor = initSensor()
        sensor.save(flush: true)
        long id = sensor.id

        // save mock file
        String dir = uploadDir + Sensor.DIR_SENSORS + id
        Files.createDirectories(new File(dir).toPath())
        File mockFile = new File(dir + "/sonar.bin")
        mockFile.write("bar")
        assert mockFile.exists()

        assert sensor.deleteFile()
        assert !mockFile.exists()

    }


}
