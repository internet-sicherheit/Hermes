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

package net.ifis.ites.hermes.management

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import net.ifis.ites.hermes.security.User
import org.grails.datastore.mapping.query.api.Criteria
import org.springframework.web.multipart.MultipartFile

import java.sql.Time

/**
 * A job represents the task to analyze a given malware sample with a given sensor on
 * a number of different virtual machines.
 *
 * This can be understood as a configuration which can be used to run the same job on a number of
 * different nodes.
 *
 * @author Kevin Wittek
 */
class Job {

    /** The time in minutes after which a job is considered list. */
    static final int JOB_LOST_TIMEOUT = 120

    /** The directory that is used to save the log files. */
    static final String DIR_PUBLISHED_JOB = "publishedJobLogs/"

    static final List<JobState> ACTIVE_STATES = [JobState.SUBMITTED, JobState.PROCESSING, JobState.PUBLISHED]

/** The different states a job can have. */
    enum JobState {
        PUBLISHED,
        SUBMITTED,
        PROCESSING,
        SUCCESS,
        FAILURE,
        PENDING
    }


    /** A short descriptive name for this job. */
    String name

    /** The used sensor. */
    Sensor sensor

    /** The used malware sample. */
    Sample sample

    /** The virtual machine on which this job has to run. */
    VirtualMachine virtualMachine

    /** The time which should be simulated while running this job. Null of not used. */
    Date simulatedTime

    /** The amount of time the job can take before it is considered to have timed out. */
    Time timeout

    /** The time at which the job is allowed to be published. */
    Date startTime
    
    /** Time to run after an reboot should be used in VM **/
    Time reboot

    /** The priority that is used when submitting the job to cuckoo. */
    int priority

    /** True if the job should create a memory dump. */
    boolean memoryDump

    /** The current status of the job, default is "pending". */
    JobState state = JobState.PENDING

    /** The error log message from cuckoo if the job has failed. */
    String errorLog

    /** The name of the log file. */
    String logFileName

    /** The name of the report file. */
    String reportFileName

    /** The date on which the job was published. */
    Date publishingDate

    /** The user that created the job. If this is set to null, than this means, the job is a public job. */
    User owner

    /** The node in which the Job ran. */
    net.ifis.ites.hermes.publishing.Node node

    def grailsApplication

    static constraints = {
        name blank: false
        simulatedTime nullable: true
        sensor nullable: true
        startTime nullable: true
        reboot nullable: true
        errorLog nullable: true
        logFileName nullable: true
        reportFileName nullable: true
        publishingDate nullable: true
        node nullable: true
        owner nullable: true
    }


    static mapping = {
        id generator: 'sequence', params: [sequence: 'job_sequence']
    }

    /**
     * Finds all published jobs with an given Jobstate
     *
     * @params state
     *      The state to find
     *
     * @params owner
     *      The owner of the jobs. Null if you want all jobs!
     *
     * @return
     *      Returns all published job with his state
     **/
    static List<Job> findAllForState(JobState state, User owner=null) {
        Criteria c = createCriteria()
        def results

        if (owner) {

            results = c.list {
                eq "state", state
                or {
                    eq 'owner', owner
                    isNull 'owner'
                }
            }
        } else {
            results = c.list {
                eq "state", state
            }
        }

        return results
    }

    /**
     * Give the path to the log file on the filesystem as a string.
     *
     * @return
     *      The path to the log file.
     */
    public String getLogFileLocation() {
        getLogDir() + logFileName
    }

    /**
     * Give the path to the report file on the filesystem as a string.
     *
     * @return
     *      The path to the report file.
     */
    public String getReportFileLocation() {
        getLogDir() + reportFileName // for now we save it in the same directory
    }

    public String getReportFileUrl() {
        grailsApplication.config.hermes.urlDownload + DIR_PUBLISHED_JOB + id + "/" + reportFileName
    }

    /**
     * Give the path to the directory which contains the log file.
     *
     * @return
     *      The path to the directory as a string.
     */
    protected String getLogDir() {
        grailsApplication.config.hermes.dirUpload + DIR_PUBLISHED_JOB + id + "/"
    }

    /**
     * Give the url for download the log file.
     *
     * @return
     *      Url of the log file as a string.
     */
    public String getLogFileUrl() {
        grailsApplication.config.hermes.urlDownload + DIR_PUBLISHED_JOB + id + "/" + logFileName
    }

    /**
     * Writes the given file into the logfile upload directory.
     * The file is stored inside a directory named after the object id.
     *
     * @param file
     *      The logfile that is written to the upload directory.
     *
     * @return
     *      The file that has been written.
     *
     * @throws IOException
     *      If an error occurs while writing the file.
     */
    public File writeLogFile(MultipartFile file) throws IOException {
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_PUBLISHED_JOB + id + "/")
        File logFile = FileStorage.writeFile(file, uploadDir, logFileName)

        return logFile
    }

    public File writeReportFile(MultipartFile file) throws IOException {
        // let's just save it in the log file directory for now
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_PUBLISHED_JOB + id + "/")
        File reportFile = FileStorage.writeFile(file, uploadDir, reportFileName)

        return reportFile
    }

    /**
     * The compareTo() method to sort PublishedJobs
     *
     * @return the ascending order from the PublishedJob
     */
    public int compareTo(Job compareJob) {

        int id = compareJob.id

        //ascending order
        return this.id - id
    }

    /**
     * A job is public, if it has no owner.
     *
     * @return true of the job has no owner, else false.
     */
    public boolean isPublic() {
        return owner == null
    }

    @Override
    public String toString() {
        return name
    }

    /**
     * Gives a list of Jobs that are considered lost.
     *
     * This means they have been published and have not been finished yet, but they are running a considerably
     * longer time as they should.
     *
     * @return The list of lost Jobs.
     */
    static List<Job> findAllLostJobs() {

        Date timeoutDate = new Date()
        use (TimeCategory) {
            timeoutDate = timeoutDate - JOB_LOST_TIMEOUT.minutes
        }

        List<Job> lostJobs = findAllByStateInList(ACTIVE_STATES)
        // TODO: move this into a single query
        lostJobs = lostJobs.findAll {j ->

            def currentTimeoutDate = timeoutDate

            use (TimeCategory) {
                currentTimeoutDate = currentTimeoutDate - j.timeout.hours - j.timeout.minutes - j.timeout.seconds
            }

            j.publishingDate.before(currentTimeoutDate)
        }

        return lostJobs
    }

    static List<Job> findAllPublicAndOwnerJobs(User owner) {
        findAllByOwnerOrOwnerIsNull(owner)
    }

    static List<Job> findAllBySampleAndSensorsForOwner(Sample sample, List sensors, User owner) {

        List<Job> foundJobs = []

        if (sensors.empty) {
            foundJobs.addAll(withCriteria {
                eq 'sample', sample
                or {
                    eq 'owner', owner
                    isNull 'owner'
                }
            })
        } else {
            for (sensorId in sensors) {
                if (sensorId == 'null') {
                    foundJobs.addAll(withCriteria {
                        eq 'sample', sample
                        isNull 'sensor'
                        or {
                            eq 'owner', owner
                            isNull 'owner'
                        }
                    })
                } else {
                    Sensor sensor = Sensor.get(sensorId)
                    foundJobs.addAll(withCriteria {
                        eq 'sample', sample
                        eq 'sensor', sensor
                        or {
                            eq 'owner', owner
                            isNull 'owner'
                        }
                    })
                }
            }
        }

       return foundJobs
    }
}
