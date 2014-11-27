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


/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(NodeStatusData)
@Build(Node)
class NodeStatusDataTests {

    void testConstraints() {

        // create valid NodeStatusData
        Node mockNode = Node.build()
        NodeStatusData statusData = new NodeStatusData(
                cpuLoadAvg1: 1.0,
                cpuLoadAvg5: 2.0,
                cpuLoadAvg15: 3.0,
                timestamp: new Date(),
                node: mockNode
        )
        assert statusData.validate()


        // test not nullable constraint for node property
        statusData.node = null
        assert !statusData.validate()
        assert 1 == statusData.errors.errorCount
        assert "nullable" == statusData.errors["node"].code

    }

}
