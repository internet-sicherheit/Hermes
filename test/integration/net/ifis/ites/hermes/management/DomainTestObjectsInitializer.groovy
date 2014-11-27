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

import java.sql.Time

/**
 * Helper class that is used to encapsulate the initiation
 * of valid test data.
 * @author Kevin Wittek
 */
class DomainTestObjectsInitializer {

    static Job initJob() {

        VirtualMachine vm = initVm(initHypervisor("hyper"))

        Job job = new Job(
                name: "job1",
                description: "foobar",
                simulatedTime: new Time(0),
                minStartTime: new Time(0),
                maxStartTime: new Time(0),
                timeout: new Time(0),
                priority: 1,
                memoryDump: true,
                emailNotificationAddress: "foo@bar.de",
                available: true,
                sensor: initSensor(),
                sample: initSample(),
                virtualMachine: vm
        )
        job.save()

        if (job.hasErrors()) {
            throw new Exception("initJob")
        }

        return job
    }

    static Sensor initSensor() {
        Sensor sensor = new Sensor(
                name: "sensor",
                description: "foobar",
                type: "sensorType",
                originalFilename: "sensor.sens",
                md5: "123"
        )

        sensor.save()

        if (sensor.hasErrors()) {
            throw new Exception("initSensor")
        }

        return sensor
    }

    static Sample initSample() {
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
            throw new Exception("initSample")
        }

        return sample
    }

    static VirtualMachine initVm(Hypervisor hyper) {

        OperatingSystem os = new OperatingSystem(
                name: "os",
                description: "desc",
                meta: "meta",
                type: "type"
        )
        os.save()

        VirtualMachine vm = new VirtualMachine(
                name: "vm",
                originalFilename: "os.img",
                os: os,
                description: "bubu",
                hypervisor: hyper)
        vm.save()

        if (vm.hasErrors()) {
            throw new Exception("initVm")
        }

        return vm
    }

    static Hypervisor initHypervisor(String name) {
        Hypervisor hyper = new Hypervisor(name: name)
        hyper.save()

        if (hyper.hasErrors()) {
            throw new Exception("initHyper")
        }

        return hyper

    }

}
