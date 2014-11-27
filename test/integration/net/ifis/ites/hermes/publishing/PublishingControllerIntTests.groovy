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

import net.ifis.ites.hermes.management.DomainTestObjectsInitializer
import net.ifis.ites.hermes.management.Hypervisor
import net.ifis.ites.hermes.management.Job
import net.ifis.ites.hermes.management.VirtualMachine
import org.springframework.mock.web.MockMultipartFile

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
class PublishingControllerIntTests extends GroovyTestCase {

    void testRegisterNodeAndPollJobs_noJobs_jsonResponseEmptyList() {

        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List jobs = controller.response.json.publishedJobs
        assert jobs.isEmpty()

    }

    void testRegisterNodeAndPollJobs_oneJob_jsonResponseJob() {
        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        Job job = DomainTestObjectsInitializer.initJob()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List pubJobs = controller.response.json.publishedJobs

        assert pubJobs.size() == 1
        assert pubJobs[0].job.id == job.id
    }

    void testRegisterNodeAndPollJobs_oneJobWithoutSensor_jsonResponseJobWithoutSensor() {
        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        Job job = DomainTestObjectsInitializer.initJob()
        job.virtualMachine.hypervisor = hyper
        job.sensor = null
        job.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List pubJobs = controller.response.json.publishedJobs

        assert pubJobs.size() == 1
        assert pubJobs[0].job.id == job.id
        assert !pubJobs[0].job.sensor
    }

    void testRegisterNodeAndPollJobs_oneJob_jsonResponsePathToSample() {

        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        Job job = DomainTestObjectsInitializer.initJob()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        String sampleUrl = "/upload-test/samples/" + job.sample.id + "/" + job.sample.originalFilename

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List jobs = controller.response.json.publishedJobs

        assert sampleUrl == jobs.get(0).job.sample.fileUrl

    }

    void testRegisterNodeAndPollJobs_oneJob_jsonResponseMd5OfSensor() {
        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        Job job = DomainTestObjectsInitializer.initJob()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        String md5 = job.sensor.md5

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List jobs = controller.response.json.publishedJobs

        assert md5 == jobs.first().job.sensor.md5
    }

    void testRegisterNodeAndPollJobs_manyJobs_jsonResponseMaxJobQueueJobs() {

        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        for (int i = 0; i < PublishingService.MAX_JOB_QUEUE_SIZE + 5; i++) {
            Job job = Job.build(virtualMachine: VirtualMachine.build(hypervisor: hyper))
            job.save(flush: true)
        }

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK
        List jobs = controller.response.json.publishedJobs

        assert PublishingService.MAX_JOB_QUEUE_SIZE == jobs.size()
    }

    void testRegisterNodePollJobFailJob_errorLogGiven_errorLogAvailalbe() {

        Hypervisor hyper = new Hypervisor(name: "virtualbox")
        hyper.save()

        Job job = DomainTestObjectsInitializer.initJob()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.pollJobs(nodeId)
        assert controller.response.json.code == PublishingController.STATUS_OK

        List jobs = controller.response.json.publishedJobs
        int jobId = jobs.first().id

        controller.response.reset()

        String errorMsg = "error"

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        controller.request.addFile(mockFile)

        controller.notifyFailJob(nodeId, jobId, errorMsg)

        Job Job = Job.get(jobId)

        assert Job.errorLog == errorMsg


    }

    void testRegisterNodeAndNotifyNodeAvgLoad_dataGiven_nodeStatusDataCreatedWithGivenData() {

        Hypervisor hyper = Hypervisor.build()
        hyper.save()

        Job job = Job.build()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        float load1 = 1.0
        float load5 = 5.0
        float load15 = 15.5
        controller.notifyNodeAvgLoad(nodeId, load1, load5, load15)
        assert controller.response.json.code == PublishingController.STATUS_OK

        Node nodeInstance = Node.get(nodeId)
        assert nodeInstance.nodeStatusData.size() == 1

        NodeStatusData nodeStatusDataInstance = nodeInstance.nodeStatusData.first()
        assert nodeStatusDataInstance.id
        assert nodeStatusDataInstance.cpuLoadAvg1 == load1
        assert nodeStatusDataInstance.cpuLoadAvg5 == load5
        assert nodeStatusDataInstance.cpuLoadAvg15 == load15
    }

    void testRegisterNodeAndNotifyNodeAvgLoad_multipleNodeDataGivenInSequence_nodeStatusDataIsMaintained() {
        Hypervisor hyper = Hypervisor.build()
        hyper.save()

        Job job = Job.build()
        job.virtualMachine.hypervisor = hyper
        job.save(flush: true)

        PublishingController controller = new PublishingController()

        controller.registerNode("node1", "1.1.1", "meta", hyper.id + "")
        assert controller.response.json.code == PublishingController.STATUS_OK
        int nodeId = controller.response.json.node.id
        controller.response.reset()

        controller.notifyNodeAvgLoad(nodeId, 0, 0, 0)
        int statusId1 = controller.response.json.nodeStatusData.id
        controller.response.reset()

        controller.notifyNodeAvgLoad(nodeId, 0, 0, 0)
        int statusId2 = controller.response.json.nodeStatusData.id
        controller.response.reset()

        controller.notifyNodeAvgLoad(nodeId, 0, 0, 0)
        int statusId3 = controller.response.json.nodeStatusData.id
        controller.response.reset()


        Node nodeInstance = Node.get(nodeId)

        assert nodeInstance.nodeStatusData.contains(NodeStatusData.get(statusId1))
        assert nodeInstance.nodeStatusData.contains(NodeStatusData.get(statusId2))
        assert nodeInstance.nodeStatusData.contains(NodeStatusData.get(statusId3))
    }

    void testNotifyFinishJob_idGiven_JobHasStatusSuccess() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)
        MockMultipartFile mockFile2 = new MockMultipartFile("reportFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.request.addFile(mockFile2)
        controller.notifyFinishJob(node.id, pj.id)

        assert pj.state == Job.JobState.SUCCESS
    }

    void testNotifyFailJob_idGiven_JobHasStatusFailure() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.notifyFailJob(node.id, pj.id, "hubbabubba")

        assert pj.state == Job.JobState.FAILURE
    }

    void testNotifyFailJob_logFileUploaded_logFileUrlExists() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.notifyFailJob(node.id, pj.id, "rompepromp")

        assert pj.logFileUrl
    }

    void testNotifyFailJob_logFileUploaded_logFileIsWritten() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.notifyFailJob(node.id, pj.id, "fail")

        File writtenFile = new File(pj.logFileLocation)

        assert fileContent == writtenFile.bytes
    }

    void testNotifyFinishJob_logFileUploaded_logFileUrlExists() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)
        MockMultipartFile mockFile2 = new MockMultipartFile("reportFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.request.addFile(mockFile2)
        controller.notifyFinishJob(node.id, pj.id)

        assert pj.logFileUrl
    }

    void testNotifyFinishJob_logFileUpoloaded_logFileIsWritten() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)
        MockMultipartFile mockFile2 = new MockMultipartFile("reportFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.request.addFile(mockFile2)
        controller.notifyFinishJob(node.id, pj.id)

        File writtenFile = new File(pj.logFileLocation)

        assert fileContent == writtenFile.bytes
    }

    void testNotifyFinishJob_reportFileUploaded_reportFileIsWritten() {
        Node node = Node.build()
        node.save(flush: true)

        Job pj = Job.build(node: node)
        pj.save(flush: true)

        byte[] fileContent = "hello world!".bytes
        MockMultipartFile mockFile = new MockMultipartFile("reportFile", "foo.txt", "text", fileContent)
        MockMultipartFile mockFile2 = new MockMultipartFile("logFile", "foo.txt", "text", fileContent)

        PublishingController controller = new PublishingController()
        controller.request.addFile(mockFile)
        controller.request.addFile(mockFile2)
        controller.notifyFinishJob(node.id, pj.id)

        File writtenFile = new File(pj.reportFileLocation)

        assert fileContent == writtenFile.bytes
    }

    void testUpdateNodeState_sendStateOnline_nodeHasStateOnline() {
        Node node = Node.build()
        node.save(flush: true)

        PublishingController controller = new PublishingController()
        controller.updateNodeState(node.id, "online")

        assert node.currentState == Node.NodeState.ONLINE
    }

    void testUpdateNodeState_sendStateOffline_nodeHasStateOffline() {
        Node node = Node.build()
        node.save(flush: true)

        PublishingController controller = new PublishingController()
        controller.updateNodeState(node.id, "offline")

        assert node.currentState == Node.NodeState.OFFLINE
    }

    void testUpdateNodeState_sendStateFailure_nodeHasStateFailure() {
        Node node = Node.build()
        node.save(flush: true)

        PublishingController controller = new PublishingController()
        controller.updateNodeState(node.id, "failure")

        assert node.currentState == Node.NodeState.FAILURE
    }

    void testUpdateNodeState_sendStateOnline_statusCodeOkReturned() {
        Node node = Node.build()
        node.save(flush: true)

        PublishingController controller = new PublishingController()
        controller.updateNodeState(node.id, "online")

        assert controller.response.json.code == PublishingController.STATUS_OK
    }

    void testUpdateNodeState_sendWrongStateValue_statusCodeErrorReturned() {
        Node node = Node.build()
        node.save(flush: true)

        PublishingController controller = new PublishingController()
        controller.updateNodeState(node.id, "tmnt")

        assert controller.response.json.code == PublishingController.STATUS_ERROR
    }

}
