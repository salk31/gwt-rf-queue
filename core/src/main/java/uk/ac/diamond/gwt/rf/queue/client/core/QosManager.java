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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Manage the pipe.
 */
public class QosManager implements PipeTarget {

    private final static int TIMER_PERIOD = 1000;

    private final List<QosEntry> list = new ArrayList<QosEntry>();

    private final HandlerManager handlerManager = new HandlerManager(this);

    private BackOff backOff = new BackOff();

    private QosRequestTransport requestTransport;

    public QosManager() {
    }

    public void start() {
        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
            @Override
            public boolean execute() {
                tick();
                return true;
            }
        }, TIMER_PERIOD);
    }

    void tick() {
        if (!backOff.sleepAgain()) {
            boolean foundFailed = resetFailed();
            if (foundFailed) {
                backOff.retry();
            }
        }
        fireReady();
    }

    void preProcess() {
        Iterator<QosEntry> it = list.iterator();
        while (it.hasNext()) {
            QosEntry e = it.next();

            if (e.isReady()) {
                float score = 1.0f;
                for (QosModifier m : e.getModifiers()) {
                    score = score * m.getScore(e);
                    if (score == 0.f) {
                        it.remove();
                        break;
                    }
                }
                e.setScore(score);
            }
        }
        Collections.sort(list);
    }

    boolean isBlocked(QosEntry e) {
        for (QosModifier m : e.getModifiers()) {
            if (m.isBlocked(e)) {
                return true;
            }
        }
        return false;
    }

    void fireReady() {
        Iterator<QosEntry> it = getList().iterator();
        while (it.hasNext()) {
            QosEntry entry = it.next();

            if (entry.isReady()) {
                if (!isBlocked(entry)) {
                    entry.fire(requestTransport);
                }
            }

            if (QosEntry.State.DONE.equals(entry.getState())) {
                it.remove();
            }
        }
        if (list.isEmpty()) {
            backOff.reset();
        }

        Document.get().getDocumentElement().setPropertyString("qosPending", "" + list.size());
        handlerManager.fireEvent(new QosEvent(list, backOff.getAttemptCount(), backOff.getCountDown() * TIMER_PERIOD ));
    }


    @Override
    public void add(QosEntry e) {
        e.attachToManager(this);
        for (QosModifier m : e.getModifiers()) {
            m.add(e);
        }
        list.add(e);
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                fireReady();
            }
        });
    }

    List<QosEntry> getList() {
        preProcess();
        return list;
    }

    boolean resetFailed() {
        boolean foundFailed = false;
        for (QosEntry q : list) {
            if (QosEntry.State.FAILED.equals(q.getState())) {
                q.reset();
                foundFailed = true;
            }
        }

        return foundFailed;
    }

    public HandlerRegistration addQosEventHandler(QosEventHandler handler) {
        return handlerManager.addHandler(QosEvent.getType(), handler);
    }

    public QosRequestTransport getRequestTransport() {
        return requestTransport;
    }

    public void setRequestTransport(QosRequestTransport p) {
        this.requestTransport = p;
    }

    public void retryNow() {
        backOff.reset();
        tick();
    }
}
