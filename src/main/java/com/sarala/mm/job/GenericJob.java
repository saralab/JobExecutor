package com.sarala.mm.job;

/**
 * Abstract API Open for extension - that allows case specific Job Implementation
 * 
 * @author S
 *
 */
public abstract class GenericJob {

    public abstract int executeJob(int jobId);
}
