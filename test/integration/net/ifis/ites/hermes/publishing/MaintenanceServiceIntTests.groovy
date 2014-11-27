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
import net.ifis.ites.hermes.management.Job

import java.sql.Time


/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
class MaintenanceServiceIntTests extends GroovyTestCase {

    def maintenanceService
    def grailsApplication

    void testCleanupNodeStatusData_lessThanMaxDataAvailableOneNode_NothingChanged() {
        Node node = Node.build()
        node.save(flush: true)

        List<NodeStatusData> dataList = []
        int maxNodeStatusData = grailsApplication.config.hermes.minNodeStatusData
        for (int i = 0; i < maxNodeStatusData; i++) {
            NodeStatusData data = NodeStatusData.build(node: node)
            dataList.add(data)
        }
        node.save(flush: true)

        maintenanceService.cleanupNodeStatusData(node)


        for (NodeStatusData data in dataList) {
            assert node.nodeStatusData.contains(data)
        }

    }

    void testCleanupNodeStatusData_moreThanMaxDataAvailableOneNode_oldDataDeletedFromNode() {

        Node node = Node.build()
        node.save(flush: true)

        int maxNodeStatusData = grailsApplication.config.hermes.minNodeStatusData
        for (i in 0..maxNodeStatusData) {
            Date timestamp = new Date().plus(i)
            NodeStatusData.build(node: node, timestamp: timestamp)
        }

        // create old data
        List<NodeStatusData> oldData = []
        for (i in 1..10) {
            Date timestamp = new Date().minus(i)
            NodeStatusData data = NodeStatusData.build(node: node, timestamp: timestamp)
            oldData.add(data)
        }

        node.save(flush: true)

        maintenanceService.cleanupNodeStatusData(node)

        node.save(flush: true)

        for (NodeStatusData data in oldData) {
            assert !node.nodeStatusData.contains(data)
        }

    }



    void testCleanupNodeStatusData_maxDataAvailableOneNode_allNodeStatusDataRemoved() {
        Node node = Node.build()
        node.save(flush: true)

        int maxNodeStatusData = grailsApplication.config.hermes.minNodeStatusData
        for (i in 1..maxNodeStatusData + 100) {
            Date timestamp = new Date().plus(i)
            NodeStatusData.build(node: node, timestamp: timestamp)
        }

        node.save(flush: true)

        maintenanceService.cleanupNodeStatusData(node)

        node.save(flush: true)

        assert node.nodeStatusData.size() == maxNodeStatusData
        assert NodeStatusData.count == maxNodeStatusData
    }

    void testUpdateLostJobs_oneLostJob_hasStateFailure() {
        Time timeout = new Time(0, 5, 0)
        Date oldDate = new Date()
        use (TimeCategory) {
            oldDate = oldDate - Job.JOB_LOST_TIMEOUT.minutes - timeout.minutes
        }

        Job job = Job.build(state: Job.JobState.PUBLISHED, timeout: timeout, publishingDate: oldDate)

        maintenanceService.updateLostJobs()

        job = Job.get(job.id)

        assert job.state == Job.JobState.FAILURE
    }
}
