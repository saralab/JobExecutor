package com.sarala.mm.job;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * A concrete implementation of the {@link GenericJob} that implements Callable
 * 
 * Using a Callable implementation let's us get a handle on the result
 * 
 * @author S
 *
 */
public class CallableJob extends GenericJob implements Callable<Integer> {

    private String userName;

    private Integer jobId;

    public CallableJob(String userName, Integer jobId) {
        this.userName = userName;
        this.jobId = jobId;
    }

    @Override
    public Integer call() throws Exception {
        return executeJob(jobId);
    }

    public final String getJobName() {
        return userName;
    }

    public final void setJobName(String jobName) {
        this.userName = jobName;
    }

    public final Integer getJobId() {
        return jobId;
    }

    public final void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    @Override
    public int executeJob(int jobId) {
        Random random = new Random();
        return random.nextInt();
    }

}
