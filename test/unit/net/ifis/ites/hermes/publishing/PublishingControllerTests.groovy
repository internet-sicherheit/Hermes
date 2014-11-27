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

package net.ifis.ites.hermes.publishing

import grails.test.mixin.*
import net.ifis.ites.hermes.management.Hypervisor

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(PublishingController)
@Mock([Hypervisor])
class PublishingControllerTests {

    void testGetHypervisors_noHypervisor_jsonResponseEmptyList() {
        controller.getHypervisors()

        assert response.json.status == "ok"
        assert response.json.hypervisors == []
    }

    void testGetHypervisors_oneHypervisor_jsonResponseOneListElement() {
        Hypervisor hyper1 = new Hypervisor(name: "kvm")
        hyper1.save()

        controller.getHypervisors()

        assert response.json.status == "ok"

        List hypervisors = response.json.hypervisors

        assert hypervisors.size() == 1
        assert hypervisors.find {it.id == hyper1.id} // custom contains

    }


    void testGetHypervisors_twoHypervisor_jsonResponseTwoListElements() {
        Hypervisor hyper1 = new Hypervisor(name: "kvm")
        hyper1.save()

        Hypervisor hyper2 = new Hypervisor(name: "vb")
        hyper2.save()

        controller.getHypervisors()

        assert response.json.status == "ok"

        List hypervisors = response.json.hypervisors

        assert hypervisors.size() == 2
        assert hypervisors.find {it.id == hyper1.id} // custom contains
        assert hypervisors.find {it.id == hyper2.id} // custom contains
    }





}
