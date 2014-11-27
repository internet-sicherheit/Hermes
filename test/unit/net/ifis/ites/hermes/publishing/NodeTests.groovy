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

import grails.buildtestdata.mixin.Build
import grails.test.mixin.*
import net.ifis.ites.hermes.management.Hypervisor
import net.ifis.ites.hermes.management.Job

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(Node)
@Build(Job)
class NodeTests {

    Node initNode() {
        Node node = new Node(
                name: "node",
                networkAddress: "123",
                metadata: "meta",
                hypervisor: new Hypervisor(
                        name: "hyperhyper"
                )

        )

        return node
    }

    void testConstraints() {
        // test creating valid node
        Node node = initNode()
        assert node.validate()

        // test blank constraints
        node.name = ""
        assert !node.validate()
        assert 1 == node.errors.errorCount
        assert "blank" == node.errors["name"].code

        node = initNode()
        node.networkAddress = ""
        assert !node.validate()
        assert 1 == node.errors.errorCount
        assert "blank" == node.errors["networkAddress"].code

    }

    void testIsJobAssociated_jobIsAssociated_True() {
        Node node = initNode()
        Job job = Job.build()

        // workaround for #GRAILS-9882, said to be fixed in current release, but is not :(
        Node.metaClass.addToJobs = { pJob ->
            jobs = jobs ?: []
            jobs << pJob
        }

        node.addToJobs(job)

        assert node.isJobAssociated(job)
    }

    void testIsJobAssociated_jobIsNotAssociated_False() {
        Node node = initNode()
        Job job = new Job()

        assert !node.isJobAssociated(job)
    }


}
