package com.sarala.mm.job.constants;

/**
 * Placeholder to hold various RESULT types. Provides a {@link #DEFAULT} value of -1 for now
 * 
 * @author S
 *
 */
public enum Result {

    DEFAULT(-1);

    private final int value;

    Result(final int resultValue) {
        value = resultValue;
    }

    public int getValue() {
        return value;
    }

}
