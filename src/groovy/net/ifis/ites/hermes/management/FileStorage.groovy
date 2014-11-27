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

import java.nio.file.Files

/**
 * The FileStorage can be used to write static files to the filesystem.
 *
 * @author Kevin Wittek
 */
class FileStorage {

    /**
     * Write the given file (as received from a http-request) to the
     * specified directory with the given filename.
     *
     * @param file
     *      The file which is written.
     * @param directory
     *      The target directory.
     * @param filename
     *      The file is saved under this filename.
     *
     * @return
     *      The written file.
     *
     * @throws IOException
     *      If an error occurs during writing.
     */
    public static File writeFile(MultipartFile file, File directory, String filename) throws IOException {

        Files.createDirectories(directory.toPath())

        File localFile = new File(directory, filename)
        file.transferTo(localFile)

        return localFile
    }

    /**
     * Writes the given string to the specified
     * directory with the given filename.
     *
     * @param content
     *      The content that is written to the file.
     * @param directory
     *      The directory in which the file is stored.
     * @param filename
     *      The name of the file.
     *
     * @return
     *      The written file.
     *
     * @throws IOException
     *      If an error occurs during writing.
     */
    public static File writeString(String content, File directory, String filename) throws IOException {

        Files.createDirectories(directory.toPath())

        File output = new File(directory, filename)
        output.write(content)

        return output

    }


}
