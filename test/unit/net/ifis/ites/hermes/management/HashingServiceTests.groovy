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
import net.ifis.ites.hermes.management.HashingService
import org.springframework.mock.web.MockMultipartFile

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(HashingService)
class HashingServiceTests {

    void testGenerateHashes_contentHelloWorld_correctMd5() {
        byte[] fileContent = "hello world".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        HashContainer result = service.generateHashes(mockFile)

        assert result.md5 == "5eb63bbbe01eeed093cb22bb8f5acdc3"
    }

    void testGenerateHashes_contentHelloWorld_correctSha1() {

        byte[] fileContent = "hello world".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        HashContainer result = service.generateHashes(mockFile)

        assert result.sha1 == "2aae6c35c94fcfb415dbe95f408b9ce91ee846ed"
    }

    void testGenerateHashes_contentHelloWorld_correctSha256() {

        byte[] fileContent = "hello world".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        HashContainer result = service.generateHashes(mockFile)

        assert result.sha256 == "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"
    }

    void testGenerateHashes_contentHelloWorld_correctSha512() {

        byte[] fileContent = "hello world".bytes
        MockMultipartFile mockFile = new MockMultipartFile("foo", "foo.txt", "text", fileContent)

        HashContainer result = service.generateHashes(mockFile)

        assert result.sha512 == "309ecc489c12d6eb4cc40f50c902f2b4d0ed77ee511a7c7a9bcd3ca86d4cd86f989dd35bc5ff499670da34255b45b0cfd830e81f605dcf7dc5542e93ae9cd76f"
    }

}
