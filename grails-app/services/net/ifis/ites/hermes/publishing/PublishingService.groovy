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
import org.springframework.web.multipart.MultipartFile

/**
 * This service is used to handle the business logic for the publishing system.
 *
 * @author Kevin Wittek
 */
class PublishingService {

    static final int MAX_JOB_QUEUE_SIZE = 5

    /**
     * Creates a new node with the given parameters.
     *
     * @param name
     *      The name of the node.
     * @param metadata
     *      Some descriptive metadata for the node.
     * @param hyper
     *      The hypervisor which is running on this node.
     *
     * @return
     *      The created node.
     */
    Node createNode(String name, String networkAddress, String metadata, List<Hypervisor> hypervisors) {
        Node node = new Node([
                name : name,
                metadata : metadata,
                networkAddress : networkAddress,
                hypervisors : hypervisors
        ])
        node.save()

        return node
    }

    List<Job> publishJobs(Node node) {

        def pendingJobs = Job.findAllForState(Job.JobState.PENDING)

        // find all jobs for hypervisor of the given node
        pendingJobs = pendingJobs.findAll {
            node.hypervisors.contains(it.virtualMachine.hypervisor)
        }

        if (pendingJobs.size() > MAX_JOB_QUEUE_SIZE) {
            pendingJobs.sort {
                -it.priority
            }

            pendingJobs = pendingJobs.subList(0, MAX_JOB_QUEUE_SIZE)
        }

        pendingJobs.each {
            publishJob(node, it)
        }

        return pendingJobs
    }

    Job startJob(Job job) {
        job.state = Job.JobState.SUBMITTED
        job.save()
        return job
    }

    Job finishJob(Job job, MultipartFile logFile, MultipartFile reportFile) {
        job.state = Job.JobState.SUCCESS
        try {
            job.logFileName = logFile.originalFilename
            job.writeLogFile(logFile)
        } catch (IOException e) {
            job.errors.reject("publishedJob.logFile.ioException",
                    "error while writing log file")
        }

        try {
            job.reportFileName = reportFile.originalFilename
            job.writeReportFile(reportFile)
        } catch (IOException e) {
            job.errors.reject("publishedJob.reportFile.ioException",
                    "error while writing report file")
        }

        job.save()
        return job
    }

    Job failJob(Job job, String errorLog, MultipartFile logFile) {
        job.state = Job.JobState.FAILURE
        job.errorLog = errorLog

        try {
            job.logFileName = logFile.originalFilename
            job.writeLogFile(logFile)
        } catch (IOException e) {
            job.errors.reject("publishedJob.logFile.ioException",
                    "error while writing log file")
        }

        job.save()
        return job
    }

    NodeStatusData updateNodeAvgLoad(Node node, float loadAvg1, float loadAvg5, float loadAvg15) {

        NodeStatusData nodeData = new NodeStatusData(
                cpuLoadAvg1: loadAvg1,
                cpuLoadAvg5: loadAvg5,
                cpuLoadAvg15: loadAvg15,
                timestamp: new Date()
        )
        node.addToNodeStatusData(nodeData)
        nodeData.save()
        return nodeData
    }

    Node updateNodeState(Node node, Node.NodeState state) {
        node.currentState = state
        node.save()
        return node
    }


    private Job publishJob(Node node, Job job) {

        job.node = node
        job.publishingDate = new Date()
        job.state = Job.JobState.PUBLISHED

        job.save()

        node.addToJobs(job)
        node.save()

        return job
    }
}
