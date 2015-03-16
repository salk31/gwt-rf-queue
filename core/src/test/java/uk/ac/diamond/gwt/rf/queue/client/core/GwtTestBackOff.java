/*
 * Diamond User Administration System
 * Copyright © 2012 Diamond Light Source Ltd
 */

package uk.ac.diamond.gwt.rf.queue.client.core;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Test backOff.
 */
public class GwtTestBackOff extends GWTTestCase {
    public void testInitialState() {
        BackOff backOff = new BackOff();
        assertFalse(backOff.sleepAgain());
        assertEquals(0, backOff.getAttemptCount());
    }

    public void testExpoBackOff() {
        BackOff backOff = new BackOff();

        assertFalse(backOff.sleepAgain());
        backOff.retry();
    }

    @Override
    public String getModuleName() {
        return "uk.ac.diamond.gwt.rf.queue.GwtRfQueue";
    }
}
