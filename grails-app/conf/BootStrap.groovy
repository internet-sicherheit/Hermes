
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

import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.security.Role
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.security.UserRole
import org.apache.commons.fileupload.disk.DiskFileItem
import org.apache.commons.io.FileUtils
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.sql.Time


class BootStrap {

    def grailsApplication

    def managementService

    def init = { servletContext ->
        
        development {

            setupHypervisors()
            setupUsermanagement()
            setupDefaultJob()
        }

        production {
            setupHypervisors()
            setupUsermanagement()
        }
    }


    def destroy = {
        
    }
    
    def setupHypervisors() {
        if (!Hypervisor.count) {

            def vb = new Hypervisor(name: "VirtualBox")
            vb.save()
            def kvm = new Hypervisor(name: "KVM")
            kvm.save()
            def vmWare = new Hypervisor(name: "VMWare")
            vmWare.save()
        }
    }

    def setupDefaultJob() {

        if (!Job.count) {

            // delete old upload folder
            FileUtils.deleteDirectory(new File(grailsApplication.config.hermes.dirUpload))

            Hypervisor vb = Hypervisor.findByName("VirtualBox")

            OperatingSystem os = new OperatingSystem(name: "Windows", description: "foo", meta: "bar", type: "x86")
            os.save()

            DiskFileItem sensorItem = new DiskFileItem("", "mock/exe", true, "putty.exe", 10, new File("bootstrap/putty.exe"))
            sensorItem.getOutputStream()
            CommonsMultipartFile mockSensor = new CommonsMultipartFile(sensorItem)
            Sensor sensor = managementService.createSensor("CuckooDefault", "default", "Cuckoo default sensors", mockSensor)

            DiskFileItem sampleItem = new DiskFileItem("", "mock/exe", true, "Metro.exe", 10, new File("bootstrap/Metro.exe"))
            sampleItem.getOutputStream()
            CommonsMultipartFile mockSample = new CommonsMultipartFile(sampleItem)
            Sample sample = managementService.createSample("DummySample", "bootstrap", mockSample)

            DiskFileItem vmItem = new DiskFileItem("", "mock/exe", true, "lolong-krokodil-welt.jpg", 10, new File("bootstrap/lolong-krokodil-welt.jpg"))
            vmItem.getOutputStream()
            CommonsMultipartFile mockVm = new CommonsMultipartFile(vmItem)
            VirtualMachine vm = managementService.createVirtualMachine("vm1", mockVm, "bootstrap", os, vb, null, null)

            managementService.createJob("job1", new Date(100, 0, 1, 1, 0), new Time(0, 1, 0),
                    0, false, true, vm, sensor, sample, null, null)
        }
    }

    def setupUsermanagement() {
        // setup default roles and user
        Role su, machineRole
        User machine, superuser

        if (!Role.count()) {
            su = new Role(authority:"ROLE_SUPERUSER")
            su.save()
            new Role(authority:"ROLE_USERMANAGEMENT").save()
            new Role(authority:"ROLE_SAMPLEMANAGEMENT").save()
            new Role(authority:"ROLE_JOBMANAGEMENT").save()
            new Role(authority:"ROLE_SENSORMANAGEMENT").save()
            new Role(authority:"ROLE_OSMANAGEMENT").save()
            new Role(authority:"ROLE_HYPERVISORMANAGEMENT").save()
            new Role(authority:"ROLE_VMMANAGEMENT").save()
            machineRole = new Role(authority:"ROLE_API").save()
        }

        if(!User.count()) {
            // Default Administration Details Start
            superuser = new User(username:'Superuser',password:'Superadmin',email:'superadmin@email.de',enabled:true)
            superuser.save(failOnError: true)
            
            if (!superuser.authorities.contains(su)) {
                UserRole.create(superuser, su)
            }
            
            machine = new User(username:'Machine',password:'Bl3chbu3ch$3',email:'node@internet-sicherheit.de',enabled:true)
            machine.save(failOnError: true)
            
            if (!machine.authorities.contains(machineRole)) {
                UserRole.create(machine, machineRole)
            }
            // Default Administration Details End
        }
    }
}     