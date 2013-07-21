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


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * An entry that executes arbitrary code.
 */
public abstract class CommandEntry extends QosEntry implements Command {

    private String state;

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void fire(QosRequestTransport transport) {
        state = "PENDING"; // XXX MAGIC
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                CommandEntry.this.execute();
                state = "DONE"; // XXX magic
            }

        });
    }

    @Override
    protected void reset() {
        state = null;
    }
}
