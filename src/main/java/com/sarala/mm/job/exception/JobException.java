package com.sarala.mm.job.exception;

/**
 * 
 * @author S
 *
 */
public class JobException extends Exception {

    private static final long serialVersionUID = -4470723096192664701L;

    public JobException() {
        super();
    }

    public JobException(String errorMessage) {
        super(errorMessage);
    }

}
