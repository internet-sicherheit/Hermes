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

import grails.buildtestdata.mixin.Build
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
@TestFor(Sample)
@Build(Sample)
class SampleTests {

    String uploadDir = "testUpload/"

    @Before
    void setUp() {
        grailsApplication.config.hermes.dirUpload = uploadDir
    }

    @After
    void tearDown() {
        FileUtils.deleteDirectory(new File(uploadDir))
    }

    Sample initSample() {
        Sample sample = new Sample(
                name: "sample",
                originalFilename: "foo.txt",
                fileExtension: "txt",
                fileContentType: "text",
                description: "bar",
                md5: "1",
                sha1: "2",
                sha256: "3",
                sha512: "4",
                ssdeep: "5",
                crc32: "6"
        )
        sample.grailsApplication = grailsApplication
        return sample
    }

    void testConstraints() {

        // test creating a valid sample
        Sample sample = initSample()
        assert sample.validate()

        // test blank constraints
        sample = initSample()
        sample.name = ""
        assert !sample.validate()
        assert 1 == sample.errors.errorCount
        assert "blank" == sample.errors["name"].code

        sample = initSample()
        sample.originalFilename = ""
        assert !sample.validate()
        assert 1 == sample.errors.errorCount
        assert "blank" == sample.errors["originalFilename"].code
    }


    void testGetFileLocation() {

        Sample sample = initSample()
        sample.save(flush: true)
        long id = sample.id

        assert uploadDir + Sample.DIR_SAMPLES + id + "/foo.txt" == sample.getFileLocation()
    }

    void testGetDir() {
        Sample sample = initSample()
        sample.save(flush: true)
        long id = sample.id

        assert uploadDir + Sample.DIR_SAMPLES + id + "/" == sample.getDir()
    }

    void testWriteSampleFile() {

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        Sample sample = initSample()
        sample.save(flush: true)
        long id = sample.id
        File writtenFile = sample.writeSampleFile(mockFile)

        assert fileContent == writtenFile.bytes
        File expectedPath = new File(uploadDir + Sample.DIR_SAMPLES + id + "/foo.txt")
        File resultPath = new File(writtenFile.getPath())
        assert  expectedPath == resultPath
    }

    void testDeleteFile() {

        Sample sample = initSample()
        sample.save(flush: true)
        long id = sample.id

        // save mock file
        String dir = uploadDir + Sample.DIR_SAMPLES + id
        Files.createDirectories(new File(dir).toPath())
        File mockFile = new File(dir + "/foo.txt")
        mockFile.write("bar")
        assert mockFile.exists()

        assert sample.deleteFile()
        assert !mockFile.exists()

    }

    void testUniqueMD5() {

        String md5 = "123"

        Sample sample1 = Sample.build(md5: md5)
        mockForConstraintsTests(Sample, [sample1])
        Sample sample2 = Sample.buildWithoutSave(md5: md5)

        assert !sample2.validate()
        assert sample2.errors.errorCount == 1
        assert "unique" == sample2.errors["md5"]
    }

}
