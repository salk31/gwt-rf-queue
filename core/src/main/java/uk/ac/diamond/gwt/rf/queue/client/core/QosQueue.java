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
import java.util.List;


/**
 * A simple queue of QosEntries.
 */
public class QosQueue extends AbstractPipeInput {
    private PipeTarget target;

    private final List<QosModifier> modifiers = new ArrayList<QosModifier>();

    @Override
    public void add(QosEntry e) {
        e.addAll(modifiers);
        target.add(e);
    }

    public void setTarget(PipeTarget p) {
        this.target = p;
    }

    public void addModifier(QosModifier m) {
        modifiers.add(m);
    }
}
