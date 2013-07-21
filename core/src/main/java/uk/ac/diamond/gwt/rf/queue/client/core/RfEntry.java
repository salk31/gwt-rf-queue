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
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 *
 *
 * @author yjs77802
 */
class RfEntry extends QosEntry {
    private final Receiver receiver;

    private final RequestContext requestContext;

    private State state;

    @Override
    public State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }

    public RfEntry(Request request) {
        this(request.getRequestContext());
    }

    public RfEntry(Request request, Receiver receiver2) {
        this(request.getRequestContext(), receiver2);
    }

    public RfEntry(RequestContext requestContext2) {
        this(requestContext2, null);
    }

    public RfEntry(RequestContext requestContext2, Receiver receiver2) {
        this.requestContext = requestContext2;
        this.receiver = receiver2;
    }

    RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public void fire(QosRequestTransport transport) {
        setState(State.PENDING);
        transport.nextMode(new RequestTransport.TransportReceiver() {
            @Override
            public void onTransportSuccess(String payload) {
                setState(State.DONE);
                notifyChange();
            }

            @Override
            public void onTransportFailure(ServerFailure failure) {
                setState(State.FAILED);
                notifyChange();
            }
        });

        if (receiver == null) {
            requestContext.fire();
        } else {
            requestContext.fire(receiver);
        }
    }

    @Override
    protected void reset() {
        state = null;
    }
}
