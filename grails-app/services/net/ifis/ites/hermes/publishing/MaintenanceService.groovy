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

import net.ifis.ites.hermes.management.Job
import org.grails.datastore.mapping.query.api.Criteria

/**
 * The MaintenanceService is used to perform maintenance tasks for the classes inside the publishing package.
 * For example cleaning up old unused data.
 *
 * @author Kevin Wittek
 */
class MaintenanceService {

    static final String LOST_JOB_ERROR = "oh noes, this job was lost -_-"

    def grailsApplication

    /**
     * Delete all old NodeStatusData entries for all nodes.
     */
    void cleanupAllNodeStatusData() {
        List<Node> allNodeInstances = Node.getAll()

        for (Node node in allNodeInstances) {
            cleanupNodeStatusData(node)
        }
    }

    /**
     * Delete all old NodeStatusData entries for the given node.
     *
     * @param Node
     *      The NodeStatusData of the given node is deleted.
     */
    void cleanupNodeStatusData(Node node) {
        int deleteCount = node.nodeStatusData.size() - grailsApplication.config.hermes.minNodeStatusData

        if (deleteCount > 0) {

            /*
             * This is not the ideal solution. I would like to use a DetachedCriteria and do
             * a batch delete with deleteAll(), but it is not possible, to specify maxResults in a
             * DetachedCriteria. The current solution sucks for big data, since everything is loaded
             * into memory first.
             *
             * And it's not possible to delete the children in a one-to-many association and automagically remove
             * the association yadda yadda...
             *
             * See:
             * http://spring.io/blog/2010/07/02/gorm-gotchas-part-2/
             *
             */
            Criteria c = NodeStatusData.createCriteria()
            List<NodeStatusData> oldData = c.list {
                eq "node", node
                maxResults deleteCount
                order "timestamp", "asc"
                lock true // does not work, wtf? no signature of method...
            }

            for (NodeStatusData data in oldData) {
                node.removeFromNodeStatusData(data)
            }

        }
        node.save(flush: true)
    }

    /**
     * Sets the state of all lost nodes to status UNKNOWN.
     */
    void updateLostNodes() {
        List<Node> lostNodes = Node.getLostNodes()

        for (n in lostNodes) {
            n.currentState = Node.NodeState.UNKNOWN
            n.save()
        }
    }

    /**
     * Sets the state of all list jobs to FALIURE.
     */
    void updateLostJobs() {
        List<Job> lostJobs = Job.findAllLostJobs()

        lostJobs.each {
            it.state = Job.JobState.FAILURE
            it.errorLog = LOST_JOB_ERROR
            it.save()
        }
    }

}
