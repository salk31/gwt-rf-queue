/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.client.core;


/**
 * BackOff strategy.
 */
public class BackOff {
    private final static int INTERVAL_MAX = 30;

    private int attemptCount;

    private int countDown;

    private int currentInterval;

    public void reset() {
        countDown = 0;
        attemptCount = 0;
        currentInterval = 1;
    }

    public boolean sleepAgain() {
        if (countDown > 1) {
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
            currentInterval = 2;
        }
        attemptCount++;
        countDown = currentInterval;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public int getCountDown() {
        return countDown;
    }
    
    public String toString() {
        return "countDown="
             + countDown
             + " currentInterval="
             + currentInterval
             + " attemptCount="
             + attemptCount;
    }
}
