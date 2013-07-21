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


import java.util.List;

import uk.ac.diamond.gwt.rf.queue.client.core.QosRequestTransport.BatchedRequest;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * An eagerly serialised RequestContext.
 */
public class TransportEntry extends QosEntry {

    private final String payload;

    private final RequestTransport.TransportReceiver receiver;

    private State state;

    // TODO 00 unit test can create lots of these, fire when feel like it
    public TransportEntry(RequestContext requestContext, Receiver recv) {
        QosRequestTransport transport = (QosRequestTransport) requestContext.getRequestFactory().getRequestTransport();
        transport.startBatch();

        // XXX remove the need for this
        transport.nextMode(new RequestTransport.TransportReceiver() {
            @Override
            public void onTransportSuccess(String payload) {
            }

            @Override
            public void onTransportFailure(ServerFailure failure) {
            }
        });

        if (recv == null) {
            requestContext.fire();
        } else {
            requestContext.fire(recv);
        }

        List<BatchedRequest> b = transport.clear();
        payload = b.get(0).payload;
        receiver = b.get(0).receiver;
        try {
            receiver.onTransportFailure(new ServerFailure("GOO"));
        } catch (Throwable th) {
        }
    }


    TransportEntry(java.lang.String payload2, RequestTransport.TransportReceiver receiver2) {
        this.payload = payload2;
        this.receiver = receiver2;
    }


    @Override
    public void fire(QosRequestTransport transport) {
        // XXX cut n paste, move to super?
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

        transport.send(payload, receiver);
    }

    @Override
    protected void reset() {
        state = null;
    }

    @Override
    public State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
    }
}
