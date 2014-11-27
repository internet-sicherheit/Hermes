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

import groovy.time.TimeCategory
import net.ifis.ites.hermes.management.Hypervisor

/**
 * @author Kevin Wittek
 */
class NodeIntTests extends GroovyTestCase {

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

    void test_uniqueConstraintViolated_NotValid() {
        Node node1 = initNode()
        node1.save()

        Node node2 = initNode()
        node2.save()

        assert !node2.validate()
        assert 1 == node2.errors.errorCount
        assert "unique" == node2.errors["name"].code

    }

    void test_uniqueConstraintPassed_Valid() {
        Node node1 = initNode()
        node1.save()

        Node node2 = initNode()
        node2.name = node1.name + "4711"
        assert node2.validate()
    }
    
    void testGetLostNodes_noNodeAvailable_returnsEmptyList() {
        def nodes = Node.getLostNodes()
        assert nodes.isEmpty()
    }

    void testGetLostNodes_1NodeWithoutNodeStatusData_returnNode() {
        Node n1 = Node.build(name: "1", currentState: Node.NodeState.ONLINE)
        n1.save(flush: true)

        def lost = Node.getLostNodes()

        assert lost.get(0) == n1
    }

    void testGetLostNodes_1NodeWithOldStatusDataCurrentStateOffline_returnsEmptyList() {
        Node n1 = Node.build(name: "1", currentState: Node.NodeState.OFFLINE)
        n1.save(flush: true)

        def lost = Node.getLostNodes()

        assert lost.isEmpty()

    }

    void testGetLostNodes_2NodesBothNotLost_returnsEmptyList() {
        Node n1 = Node.build(name: "1", currentState: Node.NodeState.ONLINE)
        n1.save(flush: true)
        NodeStatusData data1 = NodeStatusData.build(timestamp: new Date(), node: n1)
        n1.save()


        Node n2 = Node.build(name: "2", currentState: Node.NodeState.ONLINE)
        n2.save(flush: true)
        NodeStatusData data2 = NodeStatusData.build(timestamp: new Date(), node: n2)
        n2.save(flush: true)


        def lost = Node.getLostNodes()

        assert lost.isEmpty()
    }

    void testGetLostNodes_2NodesOneLost_returnsLostNode() {
        Node lostNode = Node.build(name: "1", currentState: Node.NodeState.ONLINE)
        lostNode.save(flush: true)
        Date oldDate = new Date()

        use(TimeCategory) {
            oldDate = oldDate - Node.NODE_LOST_TIMEOUT.minutes
        }

        NodeStatusData data1 = NodeStatusData.build(timestamp: oldDate, node: lostNode)
        lostNode.save()

        Node n2 = Node.build(name: "2", currentState: Node.NodeState.ONLINE)
        n2.save(flush: true)
        NodeStatusData data2 = NodeStatusData.build(timestamp: new Date(), node: n2)
        n2.save(flush: true)

        def lost = Node.getLostNodes()

        assert lost.size() == 1
        assert lost.get(0) == lostNode
    }

    void testGetLostNodes_2NodesBothLost_returns2LostNodes() {
        Node n1 = Node.build(name: "1", currentState: Node.NodeState.ONLINE)
        n1.save(flush: true)
        Date oldDate = new Date()

        use(TimeCategory) {
            oldDate = oldDate - Node.NODE_LOST_TIMEOUT.minutes
        }

        NodeStatusData data1 = NodeStatusData.build(timestamp: oldDate, node: n1)
        n1.save()

        Node n2 = Node.build(name: "2", currentState: Node.NodeState.ONLINE)
        n2.save(flush: true)
        NodeStatusData data2 = NodeStatusData.build(timestamp: oldDate, node: n2)
        n2.save(flush: true)

        def lost = Node.getLostNodes()

        assert lost.size() == 2
        assert lost.contains(n1)
        assert lost.contains(n2)
    }

    void testGetLostNodes_nodeWithOldStatusAndNewStatus_returnsEmptyList() {
        Node n1 = Node.build(name: "1", currentState: Node.NodeState.ONLINE)
        n1.save(flush: true)
        Date oldDate = new Date()

        use(TimeCategory) {
            oldDate = oldDate - Node.NODE_LOST_TIMEOUT.minutes
        }

        NodeStatusData data1 = NodeStatusData.build(timestamp: oldDate, node: n1)
        n1.save(flush: true)
        NodeStatusData data2 = NodeStatusData.build(timestamp: new Date(), node: n1)
        n1.save(flush: true)

        def lost = Node.getLostNodes()

        assert lost.isEmpty()
    }

}
