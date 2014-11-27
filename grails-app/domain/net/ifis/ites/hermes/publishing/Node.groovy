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
import net.ifis.ites.hermes.management.Job

/**
 * Represents a physical machine, running a cuckoo node, which is used to analyze malware samples.
 *
 * @author Kevin Wittek
 */
class Node {

    /** Time in minutes, after which a node is considered lost. */
    static final int NODE_LOST_TIMEOUT = 2

    /** The different states a node can have. */
    static enum NodeState {
        ONLINE,
        OFFLINE,
        FAILURE,
        UNKNOWN,
        INIT
    }

    /** A describing name for this node. */
    String name

    /** The address under which this node is accessible via network. */
    String networkAddress

    /** Some general information that describes this node. */
    String metadata

    /** The current state of this node. */
    NodeState currentState = NodeState.INIT

    static hasMany = [
            jobs : Job,
            nodeStatusData : NodeStatusData,
            hypervisors : Hypervisor
    ]

    static constraints = {
        name blank: false, unique: true
        networkAddress blank: false
    }

    static mapping = {
        version false
        nodeStatusData cascade: 'all-delete-orphan'
    }

    /**
     * Tells if the given job is associated with this node.
     *
     * @param job
     *      The job to check.
     *
     * @return
     *      True if the job is inside the list ob published jobs. False else.
     */
    boolean isJobAssociated(Job job) {
        (this.jobs) ? this.jobs.contains(job) : false
    }

    /**
     * Gives a list of Nodes that have not contacted the server since a long long time.
     * Online nodes that have the state ONLINE can become lost.
     *
     * @return
     *      The list of Nodes to which we have lost contact.
     */
    static List<Node> getLostNodes() {

        List<Node> lostNodes = []

        Date timeoutDate = new Date()
        use (TimeCategory) {
            timeoutDate = timeoutDate - NODE_LOST_TIMEOUT.minutes
        }

        // Find all Nodes, for which the last NodeStateData has a timestamp which is older than our timeout date.
        // TODO: does not work, since grails does not support subqueries at the moment

        List<Node> onlineNodes = findAllByCurrentState(NodeState.ONLINE)

        for (Node n : onlineNodes) {

            if (n.nodeStatusData) {

                List<NodeStatusData> sortedData = n.nodeStatusData.sort { it.timestamp }

                if (sortedData.last().timestamp.before(timeoutDate)) {
                    lostNodes.add(n)
                }
            } else {
                // we assume nodes without NodeStatusData as lost
                lostNodes.add(n)
            }
        }

        lostNodes
    }

}
