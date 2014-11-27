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
 */
@TestFor(VirtualMachine)
@Mock([OperatingSystem, Hypervisor])
class VirtualMachineTests {

    String uploadDir = "testUpload/"

    @Before
    void setUp() {
        grailsApplication.config.hermes.dirUpload = uploadDir
    }

    @After
    void tearDown() {
        FileUtils.deleteDirectory(new File(uploadDir))
    }

    VirtualMachine initVm() {
        VirtualMachine vm = new VirtualMachine(
                name: "vm",
                description: "foobar",
                originalFilename: "winxp.img",
                os: new OperatingSystem(),
                hypervisor: new Hypervisor()
        )
        vm.grailsApplication = grailsApplication
        return vm
    }

    void testConstraints() {
        // test creating a valid vm
        VirtualMachine vm = initVm()
        assert vm.validate()
        

        // test blank constraints
        vm = initVm()
        vm.name = ""
        assert !vm.validate()
        assert 1 == vm.errors.errorCount
        assert "blank" == vm.errors["name"].code

        vm = initVm()
        vm.originalFilename = ""
        assert !vm.validate()
        assert 1 == vm.errors.errorCount
        assert "blank" == vm.errors["originalFilename"].code


        // test relations
        vm = initVm()
        vm.os = null
        assert !vm.validate()
        assert 1 == vm.errors.errorCount
        assert "nullable" == vm.errors["os"].code

        vm = initVm()
        vm.hypervisor = null
        assert !vm.validate()
        assert 1 == vm.errors.errorCount
        assert "nullable" == vm.errors["hypervisor"].code

    }

    void testGetFileLocation() {

        VirtualMachine vm = initVm()
        vm.save(flush: true)
        long id = vm.id

        assert uploadDir + VirtualMachine.DIR_VM + id + "/winxp.img" == vm.getFileLocation()
    }

    void testGetDir() {
        VirtualMachine vm = initVm()
        vm.save(flush: true)
        long id = vm.id

        assert uploadDir + VirtualMachine.DIR_VM + id + "/" == vm.getDir()
    }

    void testWriteVirtualMachineFile() {

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "winxp.img", "binary", fileContent)

        VirtualMachine vm = initVm()
        vm.save(flush: true)
        long id = vm.id
        File writtenFile = vm.writeImageFile(mockFile)

        assert fileContent == writtenFile.bytes
        File expectedPath = new File(uploadDir + VirtualMachine.DIR_VM + id + "/winxp.img")
        File resultPath = new File(writtenFile.getPath())
        assert expectedPath == resultPath
    }

    void testDeleteFile() {

        VirtualMachine vm = initVm()
        vm.save(flush: true)
        long id = vm.id

        // save mock file
        String dir = uploadDir + VirtualMachine.DIR_VM + id
        Files.createDirectories(new File(dir).toPath())
        File mockFile = new File(dir + "/foo.txt")
        mockFile.write("bar")
        assert mockFile.exists()

        assert vm.deleteFile()
        assert !mockFile.exists()

    }
}
