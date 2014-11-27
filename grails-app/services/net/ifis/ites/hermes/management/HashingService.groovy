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

import java.security.MessageDigest

/**
 * The HashingService is used to calculate hash values of files for a number of
 * different hashing algorithms.
 *
 * @author Kevin Wittek
 */
class HashingService {

    private static final int FILE_BUFFER_SIZE = 8192

    /**
     * Generate the hashes for the given file and store into a HashContainer.
     *
     * @param file
     *      The file to hash.
     *
     * @return
     *      A HashContainer with the hashes for the given file.
     */
    HashContainer generateHashes(MultipartFile file) {

        // TODO: It would be nice to put each algorithm into its own method and use some kind of callback interface.

        // for now we have all algorithms in one method so we only have to go through the file once
        MessageDigest md5Digest = MessageDigest.getInstance("MD5")
        MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1")
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256")
        MessageDigest sha512Digest = MessageDigest.getInstance("SHA-512")

        byte[] inputBuffer = new byte[FILE_BUFFER_SIZE]
        int readBytes = 0
        file.inputStream.with {

            while (readBytes > -1) {
                readBytes = read(inputBuffer)
                if (readBytes > -1) {
                    md5Digest.update(inputBuffer, 0, readBytes)
                    sha1Digest.update(inputBuffer, 0, readBytes)
                    sha256Digest.update(inputBuffer, 0, readBytes)
                    sha512Digest.update(inputBuffer, 0, readBytes)
                }
            }
        }

        byte[] md5sum = md5Digest.digest()
        String md5 = new BigInteger(1, md5sum).toString(16).padLeft(32, '0') // 32 chars hex

        byte[] sha1sum = sha1Digest.digest()
        String sha1 = new BigInteger(1, sha1sum).toString(16).padLeft(40, '0') // 40 chars hex

        byte[] sha256sum = sha256Digest.digest()
        String sha256 = new BigInteger(1, sha256sum).toString(16).padLeft(64, '0') // 64 chars hex

        byte[] sha512sum = sha512Digest.digest()
        String sha512 = new BigInteger(1, sha512sum).toString(16).padLeft(88, '0') // 88 chars hex



        return new HashContainer(md5: md5, sha1: sha1, sha256: sha256, sha512: sha512)
    }

    /**
     * Generates the md5 hash for the given file.
     *
     * @param file
     *      The file for which the hash is generated.
     *
     * @return
     *      The md5 hash of the file as a string.
     */
    String generateMd5(MultipartFile file) {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5")

        byte[] inputBuffer = new byte[FILE_BUFFER_SIZE]
        int readBytes = 0
        file.inputStream.with {

            while (readBytes > -1) {
                readBytes = read(inputBuffer)
                if (readBytes > -1) {
                    md5Digest.update(inputBuffer, 0, readBytes)
                }
            }
        }

        byte[] md5sum = md5Digest.digest()
        String md5 = new BigInteger(1, md5sum).toString(16).padLeft(32, '0') // 32 chars hex

        return md5
    }
}
