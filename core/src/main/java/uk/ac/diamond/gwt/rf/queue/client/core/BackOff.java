/*
 * Diamond User Administration System
 * Copyright © 2015 Diamond Light Source Ltd
 */

package uk.ac.diamond.gwt.rf.queue.client.core;


/**
 *
 *
 * @author yjs77802
 */
public class BackOff {
    private final static int INTERVAL_MAX = 30;

    private int attemptCount;

    private int countDown;

    private int currentInterval;

    public void endRetry() {
        countDown = 0;
        attemptCount = 0;
        currentInterval = 1;
    }

    public boolean sleepAgain() {
        if (countDown > 0) {
            countDown--;
            return true;
        }
        return false;
    }

    public void retry() {
        if (attemptCount > 0) {
            // already in retry
            if (currentInterval < INTERVAL_MAX) {
                currentInterval *= 2;
            }
        } else {
            // start new attempt
            currentInterval = 1;
        }
        attemptCount++;
        countDown = currentInterval;
    }

    public int getAttemptCount() {
        return attemptCount;
    }
}
