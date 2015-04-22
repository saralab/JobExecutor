package com.sarala.mm.job.execute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sarala.mm.job.CallableJob;
import com.sarala.mm.job.JobStatus;
import com.sarala.mm.job.constants.Result;
import com.sarala.mm.job.constants.RunningState;
import com.sarala.mm.job.exception.JobException;
import com.sarala.mm.job.executor.JobExecutor;

public class JobExecutorTest {

    @InjectMocks
    JobExecutor unitUnderTest;

    @Mock
    ExecutorService executor;

    @Mock
    Future<Integer> expectedFuture;

    String ARBITRAY_USER_NAME = "BatMan";

    Integer ARBITRARY_JOB_ID = 4000;

    String NULL = null;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        unitUnderTest.setExecutor(executor);
    }

    @Test
    public void executeJobForUser_ExpectedUserNameWhenRunning() throws JobException {

        Map<Integer, JobStatus> currentRunningJobs = new HashMap<Integer, JobStatus>();
        JobStatus stubJobStatus = new JobStatus(ARBITRAY_USER_NAME, ARBITRARY_JOB_ID, RunningState.RUNNING, Result.DEFAULT.getValue());

        String EXPECTED_USER_NAME = ARBITRAY_USER_NAME;
        int EXPECTED_JOB_ID = ARBITRARY_JOB_ID;
        int EXPECTED_RESULT = Result.DEFAULT.getValue();

        currentRunningJobs.put(ARBITRARY_JOB_ID, stubJobStatus);

        unitUnderTest.setCurrentJobsRunning(currentRunningJobs);

        JobStatus returnedJobStatus = unitUnderTest.executeJobForUser(ARBITRAY_USER_NAME, ARBITRARY_JOB_ID);

        Mockito.verifyZeroInteractions(expectedFuture);
        Mockito.verifyZeroInteractions(executor);

        assertNotNull(returnedJobStatus);
        assertEquals(EXPECTED_USER_NAME, returnedJobStatus.getUserName());
        assertEquals(EXPECTED_JOB_ID, returnedJobStatus.getJobId());
        assertEquals(EXPECTED_RESULT, returnedJobStatus.getResult());

    }

    @Test
    public void executeJobForUser_ExpectedResultAfterRan() throws JobException, InterruptedException, ExecutionException {

        Map<Integer, JobStatus> currentRunningJobs = new HashMap<Integer, JobStatus>();
        JobStatus stubJobStatus = new JobStatus(ARBITRAY_USER_NAME, ARBITRARY_JOB_ID.intValue(), RunningState.COMPLETE, Result.DEFAULT.getValue());

        String EXPECTED_USER_NAME = ARBITRAY_USER_NAME;
        int EXPECTED_JOB_ID = ARBITRARY_JOB_ID;
        int EXPECTED_RESULT = 1000;

        currentRunningJobs.put(ARBITRARY_JOB_ID, stubJobStatus);

        unitUnderTest.setCurrentJobsRunning(currentRunningJobs);

        given(expectedFuture.isDone()).willReturn(true);
        given(executor.submit(Mockito.any(CallableJob.class))).willReturn(expectedFuture);
        given(expectedFuture.get()).willReturn(EXPECTED_RESULT);

        JobStatus returnedJobStatus = unitUnderTest.executeJobForUser(ARBITRAY_USER_NAME, ARBITRARY_JOB_ID);

        assertNotNull(returnedJobStatus);
        assertEquals(EXPECTED_USER_NAME, returnedJobStatus.getUserName());
        assertEquals(EXPECTED_JOB_ID, returnedJobStatus.getJobId());
        assertEquals(EXPECTED_RESULT, returnedJobStatus.getResult());

    }

    @Test(expected = JobException.class)
    public void executeJobForUser_UserNameEmpty() throws JobException, InterruptedException, ExecutionException {
        unitUnderTest.executeJobForUser(org.apache.commons.lang.StringUtils.EMPTY, ARBITRARY_JOB_ID);
    }

    @Test(expected = JobException.class)
    public void executeJobForUser_exceptionIfUserNameNull() throws JobException {
        unitUnderTest.executeJobForUser(NULL, ARBITRARY_JOB_ID);
    }

    @Test(expected = JobException.class)
    public void executeJobForUser_exceptionIfJobIdNull() throws JobException {
        unitUnderTest.executeJobForUser(ARBITRAY_USER_NAME, null);
    }

    @Test(expected = JobException.class)
    public void executeJobForUser_exceptionIfUserNameNullAndJobIdNull() throws JobException {
        unitUnderTest.executeJobForUser(NULL, null);
    }

}
