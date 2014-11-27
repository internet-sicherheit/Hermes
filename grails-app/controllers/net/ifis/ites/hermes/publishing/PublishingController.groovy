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
import net.ifis.ites.hermes.management.Sample
import net.ifis.ites.hermes.management.Sensor
import net.ifis.ites.hermes.management.VirtualMachine
import org.springframework.web.multipart.MultipartFile

/**
 * A webservice used for communication with the nodes.
 *
 * @author Kevin Wittek
 */
class PublishingController {

    /** Message for "ok" status. */
    static final String STATUS_MSG_OK = "ok"

    /** Code for "ok" status. */
    static final int STATUS_OK = 1

    /** Message for "error" status. */
    static final String STATUS_MSG_ERROR = "error"

    /** Code for "error" status. */
    static final String STATUS_ERROR = 100

    static final String NODE_STATE_ONLINE = "online"
    static final String NODE_STATE_OFFLINE = "offline"
    static final String NODE_STATE_FAILURE = "failure"

    /** Mappings of node state string constants, used as controller params, to the actual NodeStates. */
    static final Map<String, Node.NodeState> NODE_STATE_MAPPING = [
            (NODE_STATE_ONLINE)  :    Node.NodeState.ONLINE,
            (NODE_STATE_OFFLINE) :    Node.NodeState.OFFLINE,
            (NODE_STATE_FAILURE) :    Node.NodeState.FAILURE
    ]


    /** Dependency injected service to handle the publishing business logic. */
    def publishingService

    /**
     * Try to brew some coffee.
     * Will fail, since our server is a teapot.
     *
     * @return
     *      A sad excuse.
     */
    def brewCoffee() {
        // I can't brew coffee, I'm a teapot :(
        response.sendError(418)  
    }

    /**
     * Dummy method for testing purposes only.
     * The response contains only a status and the message which was sent.
     *
     * @param msg
     *      A dummy message which will got the response unaltered.
     *
     * @return
     *      JSON response containing the sent message.
     */
    def messageDummy(String msg) {
        render(contentType: "text/json") {[
                status : "ok",
                message : msg
        ]}
    }

    /**
     * Get a list of all available hypervisors as a JSON list.
     *
     * @return
     *      A JSON list containing all hypervisors.
     */
    def getHypervisors() {

        render(contentType: "text/json") {[
                status : STATUS_MSG_OK,
                code: STATUS_OK,
                hypervisors : Hypervisor.getAll()
        ]}
    }

    /**
     * A node can use this method to register itself inside the system.
     *
     * @param name
     *      The name of the node.
     * @param networkAddress
     *      The address under which the node is accessible.
     * @param metadata
     *      Describing metadata about the node.
     * @param hypervisorIds
     *      A comma seperated list of the ids of the hypervisors, which are running on the node.
     *
     * @return
     *      The registered node as a json document.
     */
    def registerNode(String name, String networkAddress, String metadata, String hypervisorIds) {

        String[] hypervisorIdSplit = hypervisorIds.split(",")
        List<Hypervisor> hypervisors = []
        boolean wrongId = false
        int wrongIdValue = 0
        for (String stringId in hypervisorIdSplit) {
            int id = Integer.parseInt(stringId)
            Hypervisor hypervisor = Hypervisor.get(id)
            if (hypervisor) {
                hypervisors.add(hypervisor)
            } else {
                wrongId = true
                wrongIdValue = id
                break
            }
        }

        def renderResult
        if (wrongId) {
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "hypervisor with id " + wrongIdValue + " not found"],
                    404)
        } else {

            Node nodeInstance = publishingService.createNode(name, networkAddress, metadata, hypervisors)

            if (nodeInstance.hasErrors()) {

                renderResult = renderJson([
                        status : STATUS_MSG_ERROR,
                        code : STATUS_ERROR,
                        node : nodeInstance,
                        error : nodeInstance.errors.toString()
                ])
            } else {
                renderResult = renderJson([
                        status : STATUS_MSG_OK,
                        code : STATUS_OK,
                        node : nodeInstance,
                ])
            }

        }

        renderResult

    }

    /**
     * A node can use this method to poll available jobs.
     *
     * @param nodeId
     *      The id of the polling node.
     */
    def pollJobs(long nodeId) {
        // set pessimistic lock, since we want to update the node later on
        Node nodeInstance = Node.lock(nodeId)

        def renderResult
        if (!nodeInstance) {
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node not found"],
                    404)
        } else {
            def pubJobs = publishingService.publishJobs(nodeInstance)

            def pubJobsJsonList = []
            pubJobs.each() {
                pubJobsJsonList.add(convertJobToMap(it))
            }

            renderResult = renderJson([
                    status : STATUS_MSG_OK,
                    code : STATUS_OK,
                    publishedJobs : pubJobsJsonList
            ])
        }

        renderResult
    }

    /**
     * Used by a node to notify that the job with the given id has been started.
     *
     * @param nodeId
     *      The id of the node that sent the request.
     * @param publishedJobId
     *      The id of the publishedJob that the node starts.
     *
     * @return
     *      The updated publishedJob or an error.
     */
    def notifyStartJob(long  nodeId, long  publishedJobId) {
        Node nodeInstance = Node.get(nodeId)
        Job jobInstance = Job.get(publishedJobId)

        def renderResult
        if (nodeInstance && jobInstance) {
            if(!nodeInstance.isJobAssociated(jobInstance)) {
                // node not connected to job
                renderResult = renderJson([
                        status: STATUS_MSG_ERROR,
                        code: STATUS_ERROR,
                        error: "job is not published to this node"])

            } else {
                // everything fine, update job
                jobInstance = publishingService.startJob(jobInstance)

                renderResult = renderJson([
                        status : STATUS_MSG_OK,
                        code : STATUS_OK,
                        job : jobInstance
                ])
            }
        } else {
            // node or job missing
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node and/or job not found"])
        }

        renderResult
    }



    /**
     * Used by a node to notify that the job with the given id has been finished.
     *
     * @param nodeId
     *      The id of the node that sent the request.
     * @param publishedJobId
     *      The id of the publishedJob that the node has finished.
     * @param logFile
     *      The uploaded cuckoo log file.
     *
     * @return
     *      The updated publishedJob or an error.
     */
    def notifyFinishJob(long nodeId, long publishedJobId) {
        Node nodeInstance = Node.get(nodeId)
        Job jobInstance = Job.get(publishedJobId)
        MultipartFile logFile = request.getFile("logFile")
        MultipartFile reportFile = request.getFile("reportFile")

        def renderResult
        if (nodeInstance && jobInstance) {
            if(!nodeInstance.isJobAssociated(jobInstance)) {
                // node not connected to job
                renderResult = renderJson([
                        status: STATUS_MSG_ERROR,
                        code: STATUS_ERROR,
                        error: "job is not published to this node"])

            } else {
                // everything fine, update job
                jobInstance = publishingService.finishJob(jobInstance, logFile, reportFile)

                renderResult = renderJson([
                        status : STATUS_MSG_OK,
                        code : STATUS_OK,
                        job : jobInstance
                ])
            }
        } else {
            // node or job missing
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node and/or job not found"])
        }

        renderResult
    }

    /**
     * Used by a node to notify that the job with the given id has failed.
     *
     * @param nodeId
     *      The id of the node that sent the request.
     * @param publishedJobId
     *      The id of the publishedJob that the node has failed.
     * @param errorLog
     *      The content of the log file.
     *
     * @return
     *      The updated publishedJob or an error.
     */
    def notifyFailJob(long nodeId, long publishedJobId, String errorLog) {
        Node nodeInstance = Node.get(nodeId)
        Job jobInstance = Job.get(publishedJobId)
        MultipartFile logFile = request.getFile("logFile")

        def renderResult
        if (nodeInstance && jobInstance) {
            if(!nodeInstance.isJobAssociated(jobInstance)) {
                // node not connected to job
                renderResult = renderJson([
                        status: STATUS_MSG_ERROR,
                        code: STATUS_ERROR,
                        error: "job is not published to this node"])

            } else {
                // everything fine, update job
                jobInstance = publishingService.failJob(jobInstance, errorLog, logFile)

                renderResult = renderJson([
                        status : STATUS_MSG_OK,
                        code : STATUS_OK,
                        job : jobInstance
                ])
            }
        } else {
            // node or job missing
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node and/or job not found"])
        }

        renderResult
    }

    /**
     * Used by a node to notify about the current average load.
     * A load higher than 1.00 means excessive load.
     *
     * @param nodeId
     *      The id of the node that sent the request.
     * @param loadAvg1
     *      The average load of the last minute.
     * @param loadAvg5
     *      The average load of the last 5 minutes.
     * @param loadAvg15
     *      The average load of the last 15 minutes.
     * @return
     *      Status created NodeStatusData or an error.
     */
    def notifyNodeAvgLoad(long nodeId, float loadAvg1, float loadAvg5, float loadAvg15) {

        Node nodeInstance = Node.lock(nodeId)

        def renderResult
        if (!nodeInstance) {
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node not found"],
                    404)
        } else {

            NodeStatusData statusData = publishingService.updateNodeAvgLoad(nodeInstance, loadAvg1,
                    loadAvg5, loadAvg15)
             
            // If Node Status is unknown, set node online
            if(nodeInstance.currentState != Node.NodeState.ONLINE) {
                publishingService.updateNodeState(nodeInstance, Node.NodeState.ONLINE)
            }            

            if (statusData.hasErrors()) {
                renderResult = renderJson([
                        status : STATUS_MSG_ERROR,
                        code : STATUS_ERROR,
                        node : statusData,
                        error : statusData.errors.toString()
                ])
            } else {
                renderResult = renderJson([
                        status : STATUS_MSG_OK,
                        code : STATUS_OK,
                        nodeStatusData: statusData
                ])
            }
        }

        renderResult
    }

    /**
     * Used to update the state of a node.
     *
     * Possible states a pre defined:
     * online, offline, failure
     *
     * @param nodeId
     *      The id of the node that sent the request.
     * @param state
     *      The new state of the node.
     */
    def updateNodeState(long nodeId, String state) {

        Node nodeInstance = Node.lock(nodeId)

        def renderResult
        if (!nodeInstance) {
            renderResult = renderJson([
                    status: STATUS_MSG_ERROR,
                    code: STATUS_ERROR,
                    error: "node not found"],
                    404)
        } else {
            Node.NodeState newState = NODE_STATE_MAPPING[state]
            if (newState) {
                publishingService.updateNodeState(nodeInstance, newState)
                renderResult = renderJson([
                        status: STATUS_MSG_OK,
                        code: STATUS_OK,
                        node: nodeInstance])
            } else {
                // unknown state was send
                renderResult = renderJson([
                        status: STATUS_MSG_ERROR,
                        code: STATUS_ERROR,
                        error: "unknown value for parameter 'state'"])
            }


        }

        renderResult
    }

    /**
     * Renders the given map as a JSON response.
     * The map is used to create a JSON object.
     *
     * @param content
     *      A map containing the content of the response.
     *      Is converted to a JSON object.
     * @param status
     *      The HTTP status of the response. Default value is "200".
     *
     * @return
     *      A rendered JSON response.
     */
    private Object renderJson(Map content, int status = 200) {
        render(contentType: "text/json", status: status) {content}
    }

    /**
     * Deep converts the given published job to a map.
     * The mapping is (property : value)
     *
     * This method is specifically created to render the published job inside a json
     * response. This means that unnecessary fields are omitted and some transient properties
     * are returned as well.
     *
     * @param publishedJob
     *      The published job to convert.
     *
     * @return
     *      A map representation of the published job.
     */
    private Map convertJobToMap(Job job) {

        // TODO: it would be nicer to put this into a json marshalling config

        Sensor sensor = job.sensor
        Map sensorMap = null
        if (sensor) {
            sensorMap = [
                    id: sensor.id,
                    name : sensor.name,
                    type : sensor.type,
                    description : sensor.description,
                    originalFilename : sensor.originalFilename,
                    fileUrl : sensor.fileUrl,
                    md5 : sensor.md5
            ]
        }

        Sample sample = job.sample
        Map sampleMap = [
                id: sample.id,
                name : sample.name,
                fileContentType : sample.fileContentType,
                description: sample.description,
                md5 : sample.md5,
                sha1 : sample.sha1,
                sha256 : sample.sha256,
                sha512 : sample.sha512,
                originalFilename : sample.originalFilename,
                fileUrl : sample.fileUrl
        ]

        VirtualMachine vm = job.virtualMachine
        Map virtualMachineMap = [
                id: vm.id,
                name : vm.name,
                description : vm.description,
                originalFilename : vm.originalFilename,
                fileUrl : vm.fileUrl,
                os : [
                        name : vm.os.name,
                        description : vm.os.description,
                        meta : vm.os.meta,
                        type : vm.os.type
                ],
                hypervisor : [
                        name : vm.hypervisor.name
                ]
        ]

        Map jobMap = [
                id : job.id,
                name : job.name,
                simulatedTime : job.simulatedTime.toString(),
                timeout : job.timeout.toString(),
                priority : job.priority,
                memoryDump : job.memoryDump,
                sensor : sensorMap,
                sample : sampleMap,
                virtualMachine : virtualMachineMap
        ]


        return [
                id : job.id,
                job : jobMap
        ]

    }
}
