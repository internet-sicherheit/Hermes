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

import grails.test.spock.IntegrationSpec
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Integration test class for his sample controller.
 *
 * @author Andreas Sekulski
 */
class SampleControllerIntSpec extends IntegrationSpec {

    def controller

    def g = new ApplicationTagLib()
    
    void "test deleteModal with null parameter"() {
        setup:
            controller = new SampleController()

        when:
            controller.deleteModal()

        then:
            controller.response.status == SEC.NOT_FOUND_CODE
            controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), null])
    }
    
    void "test deleteModal with invalid id"() {
        setup:
            controller = new SampleController()
            controller.params.id = 0

        when:
            controller.deleteModal()

        then:
            controller.response.status == SEC.NOT_FOUND_CODE
            controller.response.text == g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, args: [g.message(code: C.SAMPLE_LABEL_CODE), 0])
    }
    
    void "test deleteModal with valid id"() {
        setup:
            Sample sample = Sample.build()
            controller = new SampleController()
            controller.params.id = sample.id

        when:
            controller.deleteModal()

        then:
            controller.response.status == 200
            controller.response.text != null
    }
    
    void "test initDataTable"() {
        setup:
            controller = new SampleController()

        when:
            controller.initDataTable()

        then:
            controller.response.status == 200
            controller.response.json != null
    }    
}