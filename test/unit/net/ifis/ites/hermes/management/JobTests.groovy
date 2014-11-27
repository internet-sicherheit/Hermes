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

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import groovy.time.TimeCategory
import net.ifis.ites.hermes.security.User

import java.sql.Time

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 *
 * @author Kevin Wittek
 */
@TestFor(Job)
@Build([Job, User])
class JobTests {

    void testSensorIsNull_noConstraintViolation() {
        Job job = Job.build()
        job.sensor = null
        assert job.validate()
    }

    void testFindAllLostJobs_noJobs_returnsEmptyList() {
        assert Job.findAllLostJobs().empty
    }

    void testFindAllLostJobs_noLostJobsOneNormalJob_returnsEmptyList() {
        Job.build()
        assert Job.findAllLostJobs().empty
    }

    void testFindAllListJobs_oneLostJob_returnsLostJob() {
        Time timeout = new Time(0, 5, 0)
        Date oldDate = new Date()
        use (TimeCategory) {
            oldDate = oldDate - Job.JOB_LOST_TIMEOUT.minutes - timeout.minutes
        }

        Job job = Job.build(state: Job.JobState.PUBLISHED, timeout: timeout, publishingDate: oldDate)

        assert Job.findAllLostJobs()[0] == job
    }

    void testFindAllListJobs_twoLostJobsOneNormalJob_returnsTwoLostJobs() {
        Time timeout = new Time(0, 5, 0)
        Date oldDate = new Date()
        use (TimeCategory) {
            oldDate = oldDate - Job.JOB_LOST_TIMEOUT.minutes - timeout.minutes
        }

        Job job = Job.build(state: Job.JobState.PUBLISHED, timeout: timeout, publishingDate: oldDate)
        Job job2 = Job.build(state: Job.JobState.SUBMITTED, timeout: timeout, publishingDate: oldDate)

        Job newJob = Job.build()

        List<Job> lostJobs = Job.findAllLostJobs()

        assert lostJobs.size() == 2
        assert lostJobs.contains(job)
        assert lostJobs.contains(job2)
        assert !lostJobs.contains(newJob)
    }

    void testIsPublic_hasOwner_returnsFalse() {
        Job job = Job.build(owner: User.build())
        assert !job.isPublic()
    }


    void testIsPublic_hasNoOwner_returnsTrue() {
        Job job = Job.build()
        assert job.isPublic()
    }

}
