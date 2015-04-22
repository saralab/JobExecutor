package com.sarala.mm.job;

import com.sarala.mm.job.constants.RunningState;

/**
 * Wrapper class to hold the job result and status
 * 
 * <p>
 * JobStatus provides the option of returning a complex type, depending on the scenario
 * </p>
 * 
 * <ul>
 * <li>{@link #result} : holds the final result from job execution</li>
 * <li>{@link #userName}: user name of the last user who ran a given job</li>
 * <li>{@link #jobId}: Id of the job whose status is being stored.</li>
 * <li>{@link #currentState}: @see {@link RunningState}</li>
 * </ul>
 * 
 * @author S
 *
 */
public class JobStatus {

    int result;

    String userName;

    int jobId;

    RunningState currentState;

    public JobStatus(String userName, int jobId, RunningState currentState, int result) {
        this.userName = userName;
        this.jobId = jobId;
        this.currentState = currentState;
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public RunningState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(RunningState currentState) {
        this.currentState = currentState;
    }

}
