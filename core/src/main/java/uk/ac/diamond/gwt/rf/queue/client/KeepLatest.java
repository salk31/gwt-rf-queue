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
 * Discard all but the latest entry.
 */
public class KeepLatest extends QosModifier {
    private QosEntry latest;

    @Override
    void add(QosEntry e) {
        this.latest = e;
    }

    @Override
    float getScore(QosEntry e) {
        if (e == latest) {
            return 1.0f;
        }
        return 0.0f;
    }
}
