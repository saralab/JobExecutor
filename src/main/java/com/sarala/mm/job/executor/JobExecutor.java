package com.sarala.mm.job.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.sarala.mm.job.CallableJob;
import com.sarala.mm.job.JobStatus;
import com.sarala.mm.job.constants.Result;
import com.sarala.mm.job.constants.RunningState;
import com.sarala.mm.job.exception.JobException;

/**
 * Assumptions:
 * <ul>
 * <li>ONLY one instance of a given JobId can run at any time</li>
 * <li>If a user attempts to execute a current running job, the user of the current res</li>
 * <li>This is a low throughput executor, uses the same lock for reads and writes</li>
 * </ul>
 * <p>
 * Attributes:
 * </p>
 * <ul>
 * <li>{@link #runningJobsMap} : holds a {@link Integer} {@link JobStatus} of jobId and jobStatus for jobs currently
 * running. Note that we are using a HashMap instead of a ConcurrentHashMap, to enable blocking read-writes.</li>
 * <li>{@link #executor}: Executor service that can be injected into the Job Executor to run jobs</li>
 * <li>{@link #lock}: The executor uses a ReentrantLock to keep the reads and writes to the map atomic.</li>
 * </ul>
 * 
 * <ul>
 * <li>{@link #executeJobForUser(String, Integer)}: validates for null userName and jobId and throws a custom
 * {@link JobException}</li>
 * </ul>
 * @author S
 *
 */
public class JobExecutor {

    private Map<Integer, JobStatus> runningJobsMap = new HashMap<>();

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JobExecutor.class);

    private ExecutorService executor;

    private Lock lock = (Lock) new ReentrantLock();

    /**
     * Returns final {@link JobStatus} object that holds
     * 
     * <ol>
     * <li><b>Atomic: read and create job</b>
     * <ul>
     * <li>lock the {@link #runningJobsMap} to get the current status of a JobId
     * <li>if the job is currently running , return the {@link JobStatus} with userName whos is currently running the
     * job</li>
     * <li>if the there is no job currently running with the @param jobId, create a new Job with the @param jobId</li>
     * <li>add the newly created job to the {@link #runningJobsMap}</li>
     * <li>unlock {@link #runningJobsMap}</li>
     * </ul>
     * </li>
     * 
     * <li><b>Execute Job</b>
     * <ul>
     * <li>Get {@link int result} from {@link #startJobAndWaitForResult(CallableJob)}</li>
     * </ul>
     * </li>
     * 
     * <li><b>Atomic write to {@link #runningJobsMap}</b>
     * <ul>
     * <li>get the final {@link JobStatus} that holds the result, userName and currentState of the job</li>
     * </ul>
     * </li>
     * 
     * </ol>
     * 
     * @param userName
     * @param jobId
     * @return {@link JobStatus}
     * @throws JobException
     */
    public JobStatus executeJobForUser(String userName, Integer jobId) throws JobException {

        if (StringUtils.isEmpty(userName) || null == jobId) {
            log.error("Job with either null Id {} or UserName {} cannot be started ", jobId, userName);
            throw new JobException("Provide a non-null UserName :: " + userName + " and JobId :: " + jobId);
        }

        JobStatus jobStatus;
        CallableJob job;

        try {
            // Lock acquired as we need the get and subsequent put
            // to be executed as one consistent operation, any other thread
            // will block here
            lock.lock();

            jobStatus = runningJobsMap.get(jobId);

            if (isJobRunning(jobStatus)) {
                return jobStatus;
            }
            else {
                job = createNewjob(userName, jobId);
            }
        }
        finally {
            lock.unlock();
        }

        Integer result = startJobAndWaitForResult(job);

        jobStatus = getFinalJobStatus(jobId, result);

        return jobStatus;

    }

    private boolean isJobRunning(JobStatus jobStatus) {
        return (null != jobStatus && jobStatus.getCurrentState().equals(RunningState.RUNNING)) ? true : false;
    }

    private CallableJob createNewjob(String userName, Integer jobId) {

        CallableJob job = new CallableJob(userName, jobId);
        JobStatus jobStatus = new JobStatus(userName, jobId, RunningState.RUNNING, Result.DEFAULT.getValue());

        runningJobsMap.put(jobId, jobStatus);

        return job;
    }

    private Integer startJobAndWaitForResult(CallableJob job) {

        Integer finalResult = Result.DEFAULT.getValue();

        Future<Integer> future = executor.submit(job);
        try {
            while (!future.isDone()) {
                log.info("Waiting for the result");
                // Yield , to reduce CPU utilization whilst waiting
                Thread.sleep(1);
            }

            finalResult = future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Error whilst executing Job and retreiving Result {} {}", job.getJobId(), job.getJobName());
        }

        return finalResult;
    }

    private JobStatus getFinalJobStatus(Integer jobId, Integer result) {

        JobStatus currentStatus;

        try {
            lock.lock();

            currentStatus = runningJobsMap.remove(jobId);
            currentStatus.setCurrentState(RunningState.COMPLETE);
            currentStatus.setResult(result);
        }
        finally {
            lock.unlock();
        }
        return currentStatus;
    }

    // Getters and Setters

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public Map<Integer, JobStatus> getCurrentJobsRunning() {
        return runningJobsMap;
    }

    public void setCurrentJobsRunning(Map<Integer, JobStatus> currentJobsRunning) {
        this.runningJobsMap = currentJobsRunning;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
