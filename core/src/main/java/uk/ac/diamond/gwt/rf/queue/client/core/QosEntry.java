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
import java.util.Collection;
import java.util.List;


/**
 * An entry to be managed.
 */
public abstract class QosEntry implements Comparable<QosEntry> {

    enum State {
        FAILED,
        PENDING,
        DONE
    }

    private final List<QosModifier> modifiers = new ArrayList<QosModifier>();

    private float score;

    private QosManager manager;

    public float getScore() {
        return score;
    }

    protected void setScore(float score) {
        this.score = score;
    }

    protected abstract void fire(QosRequestTransport transport);

    protected abstract State getState();

    protected List<QosModifier> getModifiers() {
        return modifiers;
    }

    protected void addAll(Collection<QosModifier> modifiers2) {
        this.modifiers.addAll(modifiers2);
    }

    protected boolean isActive() {
        return !State.DONE.equals(getState()) && getState() != null;
    }

    /**
     * @return true iff this entry is ready to send
     */
    protected boolean isReady() {
        return getState() == null;
    }


    @Override
    public int compareTo(QosEntry other) {
        if (other.score < score) {
            return -1;
        }
        if (other.score > score) {
            return 1;
        }
        return 0;
    }

    protected void attachToManager(QosManager qosManager) {
        this.manager = qosManager;
    }

    protected void notifyChange() {
        manager.fireReady();
    }

    protected abstract void reset();



    /**
     *
     * @param qosModifier
     */
    public void addModifier(QosModifier p) {
        modifiers.add(p);
    }
}
