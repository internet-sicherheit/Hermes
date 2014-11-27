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
 * A sample represents a distributed file (which in most of the cases contains
 * malicious software).
 *
 * Contains meta data describing the sample as well as access to the concrete sample file.
 *
 * @author Kevin Wittek
 */
class Sample {

    def grailsApplication

    /** Dependency injected service for calculating the hash of the sample file. */
    def hashingService

    static final String DIR_SAMPLES = "samples/"

    /** The name of the sample. */
    String name

    /** The original name of the sample as it was named on the remote filesystem prior to upload. */
    String originalFilename

    /** The original extension of the sample file (e.g. exe, png). */
    String fileExtension

    /** The content type of the sample file. */
    String fileContentType

    /** A more general description of this sample. */
    String description

    /** The md5 hash of the file. */
    String md5

    /** The sha1 hash of the file. */
    String sha1

    /** The sha256 hash of the file. */
    String sha256

    /** The sha512 hash of the file. */
    String sha512

    static transients = ['fileLocation', 'fileUrl']

    static constraints = {
        name blank: false
        originalFilename blank: false
        description nullable: true
        md5 unique: true
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
     * Give the path to the directory which contains the file.
     *
     * @return
     *      The path to the directory as a string.
     */
    protected String getDir() {
        grailsApplication.config.hermes.dirUpload + DIR_SAMPLES + id + "/"
    }

    /**
     * Give the url for downloading the sensor file.
     *
     * @return
     */
    public String getFileUrl() {
        grailsApplication.config.hermes.urlDownload + DIR_SAMPLES + id + "/" + originalFilename
    }


    /**
     * Writes the given file into the sample upload directory.
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
    public File writeSampleFile(MultipartFile file) throws IOException {
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_SAMPLES + id + "/")
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
     * Generates the hash values for the given file and stores them in the
     * corresponding fields.
     *
     * @param file
     *      The file for which the hash values are generated.
     */
    public void generateHashes(MultipartFile file) {
        HashContainer hashes = hashingService.generateHashes(file)
        md5 = hashes.md5
        sha1 = hashes.sha1
        sha256 = hashes.sha256
        sha512 = hashes.sha512
    }
    
    @Override 
    public String toString() {
        return name
    }
}
