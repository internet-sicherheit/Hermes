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

import net.ifis.ites.hermes.management.Hypervisor
import net.ifis.ites.hermes.management.Job
import net.ifis.ites.hermes.management.VirtualMachine
import org.springframework.mock.web.MockMultipartFile

/**
 *
 * @author Kevin Wittek
 */
class PublishingServiceIntTests extends GroovyTestCase {

    def publishingService

    void testPublishJobs_OneJobWithFittingHypervisor_CreateJobAndReturnListWithJob() {

        VirtualMachine vm = VirtualMachine.build()

        Job job = Job.build(virtualMachine: vm)
        job.save()

        Node registeredNode = Node.build()
        registeredNode.addToHypervisors(vm.hypervisor)
        registeredNode.save()

        def pubJobs = publishingService.publishJobs(registeredNode)

        assert registeredNode.jobs?.size() == 1
        assert registeredNode.jobs?.contains(pubJobs[0])
    }

    void testPublishJobs_NoJobWithFittingHypervisor_ReturnEmptyList() {

        VirtualMachine vm = VirtualMachine.build()

        Hypervisor hyper2 = Hypervisor.build()

        Job job = Job.build(virtualMachine: vm)

        Node node = Node.build()
        node.addToHypervisors(hyper2)
        node.save()

        def pubJobs = publishingService.publishJobs(node)

        assert node.jobs == null
        assert pubJobs.isEmpty()
    }

    void testPublishJobs_MultipleJobsWithFittingHypervisor_PublishMaxSizeJobsAndReturnListWithJobs() {
        VirtualMachine vm = VirtualMachine.build()

        for (int i = 0; i < publishingService.MAX_JOB_QUEUE_SIZE * 2; i++) {
            Job.build(virtualMachine: vm)
        }

        Node node = Node.build()
        node.addToHypervisors(vm.hypervisor)
        node.save()

        def pubJobs = publishingService.publishJobs(node)

        assert pubJobs?.size() == publishingService.MAX_JOB_QUEUE_SIZE
        assert node.jobs?.size() == publishingService.MAX_JOB_QUEUE_SIZE
    }

    void testPublishJobs_MultipleJobsWithDifferentPriority_ReturnListWithMaxPriorityJobs() {
        VirtualMachine vm = VirtualMachine.build()

        int lowPriority = 1
        int highPriority = 2

        for (int i = 0; i < publishingService.MAX_JOB_QUEUE_SIZE; i++) {
            Job.build(virtualMachine: vm, priority: lowPriority)
            Job.build(virtualMachine: vm, priority: highPriority)
        }

        Node node = Node.build()
        node.addToHypervisors(vm.hypervisor)
        node.save()

        def pubJobs = publishingService.publishJobs(node)
        assert pubJobs?.size() == publishingService.MAX_JOB_QUEUE_SIZE
        pubJobs.each {
            assert it.priority == highPriority
        }

    }

    void testPublishJobs_OneJobWithFittingHypervisor_PublishedDateSet() {
        VirtualMachine vm = VirtualMachine.build()

        Job job = Job.build(virtualMachine: vm)

        Node registeredNode = Node.build()
        registeredNode.addToHypervisors(vm.hypervisor)
        registeredNode.save()

        def pubJobs = publishingService.publishJobs(registeredNode)
        def pubJob = pubJobs.get(0)

        assert pubJob.publishingDate

    }

    void testFailJob_JobStateStarted_JobStateFailed() {

        Job job = new Job()


        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        publishingService.failJob(job, "hubbabubba", mockFile)

        assert job.state == Job.JobState.FAILURE
    }

    void testFailJob_LogDataProvided_ContentIsEqual() {
        Job job = new Job()

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        String content = "ninjaturtle"
        publishingService.failJob(job, content, mockFile)

        assert job.errorLog == content
    }

    void testUpdateNodeAvgLoad_dataProvided_nodeStatusDataIsCreated() {

        Node node = Node.build()

        float load1 = 1
        float load5 = 2
        float load15 = 3

        NodeStatusData data = publishingService.updateNodeAvgLoad(node, load1, load5, load15)

        assert data
        assert data.cpuLoadAvg1 == load1
        assert data.cpuLoadAvg5 == load5
        assert data.cpuLoadAvg15 == load15
    }

    void testUpdateNodeAvgLoad_dataProvided_nodeStatusDataAndNodeAreAssociated() {

        Node node = Node.build()

        float load1 = 1
        float load5 = 2
        float load15 = 3

        NodeStatusData data = publishingService.updateNodeAvgLoad(node, load1, load5, load15)

        assert data.node == node
        assert node.nodeStatusData.toArray()[0] == data
    }

    void testUpdateNodeAvgLoad_dataProvided_timestampSet() {

        Node node = Node.build()

        float load1 = 1
        float load5 = 2
        float load15 = 3

        NodeStatusData data = publishingService.updateNodeAvgLoad(node, load1, load5, load15)

        assert data.timestamp

    }

    void testUpdateNodeState_setOnline_nodeHasStateOnline() {

        Node node = Node.build()

        publishingService.updateNodeState(node, Node.NodeState.ONLINE)

        assert node.currentState == Node.NodeState.ONLINE
    }

    void testUpdateNodeState_setOffline_nodeHasStateOffline() {

        Node node = Node.build()

        publishingService.updateNodeState(node, Node.NodeState.OFFLINE)

        assert node.currentState == Node.NodeState.OFFLINE
    }

    void testUpdateNodeState_nodeIsOfflineSetOnline_nodeIsValid() {

        Node node = Node.build(currentState: Node.NodeState.OFFLINE)

        publishingService.updateNodeState(node, Node.NodeState.ONLINE)

        assert node.validate()
    }





}
