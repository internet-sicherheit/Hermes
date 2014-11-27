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

import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.grails.commons.metaclass.GroovyDynamicMethodsInterceptor
import org.codehaus.groovy.grails.web.metaclass.BindDynamicMethod
import org.springframework.web.multipart.MultipartFile

import java.sql.Time

/**
 * The ManagementService is used to handle the business logic and
 * transactions for domain classes inside the management package.
 *
 * @author Kevin Wittek
 */
class ManagementService {

    static final String ERROR_MSG_DELETE_FILE = "Error while deleting the file"
    static final String ERROR_MSG_WRITE_FILE = "Error while writing the file"

    static final String ERROR_MSG_VALIDATE_JOB = "Error while validating the job"

    ManagementService() {
        GroovyDynamicMethodsInterceptor i = new GroovyDynamicMethodsInterceptor(this)
        i.addDynamicMethodInvocation(new BindDynamicMethod())
    }

    /**
     * Create and save a sample object with the given parameters and file.
     *
     * The file will be written to a folder corresponding the created sample.
     * It won't be stored on the filesystem if an error occurs while persisting the sample.
     *
     *
     * @param name
     *      The name of the sample.
     * @param description
     *      The description of the sample.
     * @param file
     *      The sample file which will be linked with the sample.
     *
     * @return
     *      The created sample.
     *
     * @throws FileException
     *      If an error occurs while writing the sample file.
     */
    Sample createSample(String name, String description, MultipartFile file) throws FileException {

        String fileExtension = FilenameUtils.getExtension(file.originalFilename)

        Sample sample = new Sample(
            name: name,
            description: description,
            originalFilename: file.originalFilename,
            fileContentType: file.contentType,
            fileExtension: fileExtension
        )

        sample.generateHashes(file)
        sample.save(flush: true)

        if (!sample.hasErrors()) {
            try {
                sample.writeSampleFile(file)
            } catch (IOException e) {
                // init rollback
                throw new FileException(ERROR_MSG_WRITE_FILE, e, sample)
            }
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, sample)
        }

        return sample
    }

    /**
     * Updates the given sample with the given parameters.
     * If a new sample file is specified the old sample file is deleted first.
     *
     * @param sample
     *      The sample which is updated.
     * @param name
     *      The new name or the old one if not changed.
     * @param description
     *      The new description or the old one if not changed.
     * @param file
     *      The new file, null if not changed. (optional)
     *
     * @return
     *      The updated sample instance.
     *
     * @throws FileException
     *      If an error occurs while writing the new sample file or deleting the old one.
     */
    Sample updateSample(Sample sample, String name, String description, MultipartFile file) throws FileException {

        sample.name = name
        sample.description = description
        sample.save()

        // only save a file if we have a valid sample instance and an file should be override
        if(!sample.hasErrors()) {
            if(file) {
                if(!sample.deleteFile()) {
                    // error while deleting the old file, rollback
                    throw new FileException(ERROR_MSG_DELETE_FILE, sample)
                }

                sample.originalFilename = file.originalFilename
                sample.fileContentType = file.contentType
                sample.fileExtension = FilenameUtils.getExtension(file.originalFilename)
                sample.generateHashes(file)

                try {
                    sample.writeSampleFile(file)
                } catch (IOException e) {
                    throw new FileException(ERROR_MSG_WRITE_FILE, e, sample)
                }
            }
            sample.save()
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, sample)
        }

        return sample
    }

    /**
     * Deletes the given sample as well as the associated file.
     *
     * @param sample
     *      The sample instance which should be deleted
     *
     * @throws FileException
     *      If an error occurs while deleting the file.
     */
    void deleteSample(Sample sample) throws FileException {
        sample.delete(flush: true)
        boolean success = sample.deleteFile()
        if (!success) {
            throw new FileException(ERROR_MSG_DELETE_FILE, sample)
        }

    }

    /**
     * Create and save a sensor object with the given parameters.
     *
     * The given file is stored on the filesystem.
     *
     * @param name
     *      The name of the sensor.
     * @param type
     *      The sensor type.
     * @param description
     *      A general description of the sensor.
     * @param file
     *      An uploaded file which contains the actual sensor.
     *
     * @return
     *      The created sensor.
     *
     * @throws FileException
     *      If an error occurs while writing the file.
     */
    Sensor createSensor(String name, String type, String description, MultipartFile file) throws FileException {
        Sensor sensorInstance = new Sensor(
            name: name,
            type: type,
            description: description,
            originalFilename: file.originalFilename
        )
        sensorInstance.generateHash(file)

        sensorInstance.save(flush: true)

        if (!sensorInstance.hasErrors()) {
            try {
                sensorInstance.writeSensorFile(file)
            } catch (IOException e) {
                throw new FileException(ERROR_MSG_WRITE_FILE, e, sensorInstance)
            }
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, sensorInstance)
        }

        return sensorInstance
    }

    /**
     * Updates the given sensor with the given parameters.
     * If a new sensor file is specified the old sensor file is deleted first.
     *
     * @param sensor
     *      The sensor which shall be updated.
     * @param name
     *      The new name or the old one if not changed.
     * @param type
     *      The new description or the old one if not changed.
     * @param description
     *      The new description or the old one if not changed.
     * @param file
     *      The new file, null if not changed. (optional)
     *
     * @return
     *      The updated sensor instance.
     *
     * @throws FileException
     *      If an error occurs while writing the new sensor file or deleting the old one.
     */
    Sensor updateSensor(Sensor sensor, String name, String type, String description, MultipartFile file) throws FileException {

        sensor.name = name
        sensor.type = type
        sensor.description = description

        sensor.save()

        if (!sensor.hasErrors()) {
            if (file) {
                sensor.generateHash(file)

                if(!sensor.deleteFile()) {
                    // error while deleting the old file, rollback
                    throw new FileException(ERROR_MSG_DELETE_FILE, sensor)
                }

                sensor.originalFilename = file.originalFilename

                try {
                    sensor.writeSensorFile(file)
                } catch (IOException e) {
                    throw new FileException(ERROR_MSG_WRITE_FILE, e, sensor)
                }

                sensor.save()
            }
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, sensor)
        }

        return sensor
    }

    /**
     * Deletes the given sensor as well as the associated file.
     *
     * @param sensor
     *      The sensor instance which should be deleted.
     *
     * @throws FileException
     *      If an error occurs while deleting the file.
     */
    void deleteSensor(Sensor sensor) throws FileException {
        sensor.delete(flush: true)
        boolean success = sensor.deleteFile()
        if (!success) {
            throw new FileException(ERROR_MSG_DELETE_FILE, sensor)
        }
    }

    /**
     * Create and save a new virtual machine instance.
     * The given image file is stored on the file system.
     *
     * @param name
     *      The name of the new virtual machine.
     * @param image
     *      An disk-image file that will be associated with the virtual machine.
     * @param description
     *      A general description of the virtual machine.
     * @param os
     *      The operating system which is running on the virtual machine. May be null if osParams is specified.
     * @param hyper
     *      The hypervisor which is able to run the virtual machine. May be null if hyperParams is specified.
     * @param osParams
     *      A map containing the constructor parameters to create a new operating system object. If os is null this
     *      parameter is used to create a new operating system object.
     * @param hyperParams
     *      A map containing the constructor parameters to create a new hypervisor object. If hyper is null this
     *      parameter is used to create a new hypervisor object.
     *
     * @return
     *      The created virtual machine.
     *
     * @throws FileException
     *      If an error occurs while saving the image file.
     */
    VirtualMachine createVirtualMachine(String name, MultipartFile image, String description, OperatingSystem os,
        Hypervisor hyper, Map osParams, Map hyperParams) throws FileException {

        if (!os) {
            os = new OperatingSystem(osParams)
        }

        if (!hyper) {
            hyper = new Hypervisor(hyperParams)
        }

        VirtualMachine vmInstance = new VirtualMachine(
            name: name,
            description: description,
            originalFilename: image.originalFilename,
            os: os,
            hypervisor: hyper
        )

        vmInstance.save()

        if (!vmInstance.hasErrors()) {
            try {
                vmInstance.writeImageFile(image)
            } catch (IOException e) {
                throw new FileException(ERROR_MSG_WRITE_FILE, e)
            }
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, vmInstance)
        }

        return vmInstance
    }

    /**
     * Updates the given virtual machine with the given parameters.
     * If a new image file is specified, the old image file is deleted first.
     *
     * @param vm
     *      The virtual machine which shall be updated.
     * @param name
     *      The new name, null if not changed.
     * @param image
     *      The new image file, null if not changed.
     * @param description
     *      The new description, null if not changed.
     * @param os
     *      The new operating system, null if not changed or if a new operating system should
     *      be created with the given osParams.
     * @param hyper
     *      The new hypervisor, null if not changed or if a new hypervisor should be
     *      created with the given hyperParams.
     * @param osParams
     *      A map containing the constructor parameters to create a new operating system
     *      object. If os is null this parameter is used to create a new
     *      operating system object.
     * @param hyperParams
     *       A map containing the constructor parameters to create a new hypervisor object.
     *       If hyper is null this parameter is used to create a new hypervisor object.
     *
     * @return
     *      The updated virtual machine instance.
     *
     * @throws FileException
     *      If an error occurs while writing the new image file or deleting the old one.
     */
    VirtualMachine updateVirtualMachine(VirtualMachine vm, String name, MultipartFile image, String description, OperatingSystem os,
        Hypervisor hyper, Map osParams, Map hyperParams) throws FileException {

        vm.name = name
        vm.description = description

        if (os) {
            vm.os = os
        } else if (osParams) {
            OperatingSystem operatingSystem = new OperatingSystem(osParams)
            operatingSystem.save()
            vm.os = operatingSystem
        }

        if (hyper) {
            vm.hypervisor = hyper
        } else if (hyperParams) {
            Hypervisor hypervisor = new Hypervisor(hyperParams)
            hypervisor.save()
            vm.hypervisor = hypervisor
        }

        vm.save()

        if (!vm.hasErrors()) {
            if (image) {
                if(!vm.deleteFile()) {
                    // error while deleting the old file, rollback
                    throw new FileException(ERROR_MSG_DELETE_FILE, vm)
                }

                vm.originalFilename = image.originalFilename
                try {
                    vm.writeImageFile(image)
                } catch (IOException e) {
                    throw new FileException(ERROR_MSG_WRITE_FILE, e, vm)
                }

                vm.save()
            }
        } else {
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, vm)
        }

        return vm
    }

    /**
     * Deletes the given virtual machine as well as the associated image file.
     *
     * @param vm
     *      The virtual machine instance which should be deleted.
     *
     * @throws FileException
     *      If an error occurs while deleting the file.
     */
    void deleteVirtualMachine(VirtualMachine vm) throws FileException {
        vm.delete(flush: true)
        boolean success = vm.deleteFile()
        if (!success) {
            throw new FileException(ERROR_MSG_DELETE_FILE, vm)
        }
    }

    /**
     * Create and save a new job instance.
     * If no sample and sensor are provided the data to construct a new sample and sensor
     * have to be provided in the corresponding params as a map.
     *
     * @param name
     * @param simulatedTime
     * @param timeout
     * @param priority
     * @param memoryDump
     * @param publish
     * @param vm
     * @param sensor
     * @param sample
     * @param sensorParams
     *      A map containing the constructor parameters to create a new sensor object.
     *      If sensor is null this parameter is used to create a new sensor object.
     * @param sampleParams
     *      A map containing the constructor parameters to create a new sample object.
     *      If sample is null this parameter is used to create a new sample object.
     *
     * @return
     *      The created job.
     *
     * @throws FileException
     *      If an error occurs while saving the sensor or sample file.
     */
    Job createJob(String name, Date simulatedTime,
        Time timeout, int priority, boolean memoryDump, boolean publish,
        VirtualMachine vm ,Sensor sensor, Sample sample, Map sensorParams, Map sampleParams) throws FileException {
        
        boolean newSensor = false

        // this is a longshot, but if we got the name, we should have the other params as well
        if (!sensor && sensorParams?.name) {
            String sensorName = sensorParams.name
            String sensorType = sensorParams.type
            String sensorDescription = sensorParams.description
            MultipartFile sensorFile = sensorParams.file

            // might throw FileException
            sensor = createSensor(sensorName, sensorType, sensorDescription, sensorFile)
            newSensor = true
        }
        
        boolean newSample = false
        if (!sample) {
            String sampleName = sampleParams.name
            String sampleDescription = sampleParams.description
            MultipartFile sampleFile = sampleParams.file

            // might throw FileException
            sample = createSample(sampleName, sampleDescription, sampleFile)
            newSample = true
        }
        
        Job jobInstance = new Job(
            name: name,
            timeout: timeout,
            priority: priority,
            memoryDump: memoryDump,
            sensor: sensor,
            sample: sample,
            simulatedTime: simulatedTime,
            available: publish,
            virtualMachine: vm
        )
        
        if (!jobInstance.save()) {
            
            // clean up the files
            if (newSensor) {
                sensor.deleteFile()
            }

            if (newSample) {
                sample.deleteFile()
            }

            // rollback
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, jobInstance)
        }
        
        return jobInstance
    }

    /**
     * Updates the given job with the given parameters.
     *
     * @param job
     * @param name
     * @param description
     * @param simulatedTime
     * @param timeout
     * @param priority
     * @param memoryDump
     * @param sensor
     *      The new sensor. If it should not be changed the old sensor has to be provided.
     * @param sample
     *      The new sample. If it should not be changed the old sample has to be provided.
     * @param sensorParams
     *      A map containing the constructor parameters to create a new sensor object.
     *      If sensor is null this parameter is used to create a new sensor object.
     * @param sampleParams
     *      A map containing the constructor parameters to create a new sample object.
     *      If sample is null this parameter is used to create a new sample object.
     * @param vm
     *
     * @return
     *      The updated job.
     *
     * @throws FileException
     *      If an error occurs while saving the sensor or sample file.
     */
    Job updateJob(Job job, String name, String description, Date simulatedTime,
        Time timeout, int priority, boolean memoryDump, boolean publish,
        VirtualMachine vm  ,Sensor sensor, Sample sample,
        Map sensorParams, Map sampleParams) throws FileException {

        if (!sensor && sensorParams?.name) {
            String sensorName = sensorParams.name
            String sensorType = sensorParams.type
            String sensorDescription = sensorParams.description
            MultipartFile sensorFile = sensorParams.file

            // might throw FileException
            sensor = createSensor(sensorName, sensorType, sensorDescription, sensorFile)
        }

        if (!sample) {
            String sampleName = sampleParams.name
            String sampleDescription = sampleParams.description
            MultipartFile sampleFile = sampleParams.file

            // might throw FileException
            sample = createSample(sampleName, sampleDescription, sampleFile)
        }

        Map params = [
            name: name,
            description: description,
            timeout: timeout,
            priority: priority,
            memoryDump: memoryDump,
            sensor: sensor,
            sample: sample,
            simulatedTime: simulatedTime,
            available: publish,
            virtualMachine: vm
        ]

        job.properties = params       
        if (!job.save()) {
            // rollback
            throw new ValidationException(ERROR_MSG_VALIDATE_JOB, job)
        }

        return job
    }

    /**
     * Deletes the given job.
     *
     * @param job
     *      The job to delete.
     */
    void deleteJob(Job job) {
        job.delete(flush: true)
    }
}
