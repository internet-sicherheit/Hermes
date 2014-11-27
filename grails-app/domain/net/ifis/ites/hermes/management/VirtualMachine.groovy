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
 * A virtual machine represents the concrete configuration of a
 * real virtual machine image.
 *
 * @author Kevin Wittek
 */
class VirtualMachine {

    transient grailsApplication

    static final String DIR_VM = "vms/"

    /** The name of the virtual machine. */
    String name

    /** A general description the machines features and usage. */
    String description

    /** The original filename of the image file. */
    String originalFilename

    /** The operating system which runs on this virtual machine. */
    OperatingSystem os

    /** The hypervisor that has to be used to run this virtual machine. */
    Hypervisor hypervisor

    static transients = ['fileLocation', 'fileUrl']


    static constraints = {
        name blank: false
        originalFilename blank: false
        description nullable: true
    }

    static mapping = {
        hypervisor cascade: 'save-update'
        os cascade: 'save-update'
    }

    /**
     * Give the path to the image on the filesystem as a string.
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
        grailsApplication.config.hermes.urlDownload + DIR_VM + id + "/" + originalFilename
    }

    /**
     * Give the path to the directory which contains the image file.
     *
     * @return
     *      The path to the directory as a string.
     */
    protected String getDir() {
        grailsApplication.config.hermes.dirUpload + DIR_VM + id + "/"
    }

    /**
     * Writes the given file into the vm upload directory.
     * The file is stored inside a directory named after the id.
     *
     * @param file
     *      The image file that is written to the upload directory.
     *
     * @return
     *      The file that has been written.
     *
     * @throws IOException
     *      If an error occurs while writing the file.
     */
    public File writeImageFile(MultipartFile file) throws IOException {
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_VM + id + "/")
        File sampleFile = FileStorage.writeFile(file, uploadDir, originalFilename)

        return sampleFile
    }

    /**
     * Deletes the image file associated with this VM.
     * Should only be called after deleting the VM!
     *
     * @return
     *      True if deleting was successful, false else.
     */
    public boolean deleteFile() {
        new File(getDir()).deleteDir()
    }

    @Override 
    public String toString() {
        return name
    }
}
