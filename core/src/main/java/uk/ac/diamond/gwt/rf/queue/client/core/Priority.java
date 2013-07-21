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
 * Set the priority given to a QosEntry.
 *
 * Higher values are prioritised over lower values.
 */
public class Priority extends QosModifier {
    private final float score;

    public Priority(float score2) {
        this.score = score2;
    }

    @Override
    float getScore(QosEntry e) {
        return score;
    }
}
