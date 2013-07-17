/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.client;

/**
 * Alter the QOS contract in some way.
 *
 */
public class QosModifier {
    void add(QosEntry e) {
    }

    float getScore(QosEntry e) {
        return 1.0f;
    }

    protected boolean isBlocked(QosEntry e) {
        return false;
    }
}
