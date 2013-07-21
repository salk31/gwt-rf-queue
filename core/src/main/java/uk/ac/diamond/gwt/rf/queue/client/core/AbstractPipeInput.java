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

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;

/**
 * Accept entries to be prioritised.
 */
public abstract class AbstractPipeInput implements PipeInput {

    @Override
    public void add(Request r) {
        add(new RfEntry(r));
    }

    @Override
    public void add(RequestContext r) {
        add(new RfEntry(r));
    }

    @Override
    public void add(RequestContext r, Receiver recv) {
        add(new RfEntry(r, recv));
    }
}
