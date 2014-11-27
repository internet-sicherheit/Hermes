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

import org.springframework.web.multipart.MultipartFile

/**
 * A sensor represents a sensor software component which can track
 * and log malicious software behaviour.
 *
 * @author Kevin Wittek
 */
class Sensor {

    /** The grails application is used to access the config. */
    def grailsApplication

    /** Dependency injected service to generate the md5 hash of the sensor file. */
    def hashingService

    static final String DIR_SENSORS = "sensors/"

    /** The name of the sensor. */
    String name

    /** A specific type. */
    String type

    /** A more general description of the sensor's behaviour. */
    String description

    /** The original name of the sensor  as it was named on the remote filesystem prior to upload. */
    String originalFilename

    /** The md5 hash of the sensor file. */
    String md5

    static transients = ['fileLocation', 'fileUrl']

    static constraints = {
        name blank: false
        originalFilename blank: false
        description nullable: true
        type nullable: true
    }

    /**
     * Give the path to the file on the filesystem as a string.
     *
     * @return
     *      The path to the file.
     */
    public String getFileLocation() {
        getDir() + originalFilename
    }

    /**
     * Give the url for downloading the sensor file.
     *
     * @return
     */
    public String getFileUrl() {
        grailsApplication.config.hermes.urlDownload + DIR_SENSORS + id + "/" + originalFilename
    }

    /**
     * Writes the given file into the sensor upload directory.
     * The file is stored inside a directory named after the id.
     *
     * @param file
     *      The file that is written to the upload directory.
     *
     * @return
     *      The file that has been written.
     *
     * @throws IOException
     *      If an error occurs while writing the file.
     */
    public File writeSensorFile(MultipartFile file) throws IOException {
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_SENSORS + id + "/")
        File sampleFile = FileStorage.writeFile(file, uploadDir, originalFilename)

        return sampleFile
    }

    /**
     * Deletes the sample file associated with this sample.
     * Should only be called after deleting the sample!
     *
     * @return
     *      True if deleting was successful, false else.
     */
    public boolean deleteFile() {
        new File(getDir()).deleteDir()
    }

    /**
     * Give the path to the directory which contains the sensor file.
     *
     * @return
     *      The path to the directory as a string.
     */
    protected String getDir() {
        grailsApplication.config.hermes.dirUpload + DIR_SENSORS + id + "/"
    }

    @Override 
    public String toString() {
        return name
    }

    /**
     * Generates the md5 hash for the given file and save it to this sensor.
     *
     * @param file
     *      The file for which the md5 is created.
     */
    public void generateHash(MultipartFile file) {
        md5 = hashingService.generateMd5(file)
    }
}
