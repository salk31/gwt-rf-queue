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


import java.util.List;

import uk.ac.diamond.gwt.rf.queue.client.QosRequestTransport.BatchedRequest;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * A serialised RequestContext.
 */
public class TransportEntry extends QosEntry {
    private final String payload;
    private final RequestTransport.TransportReceiver receiver;

    private String state;

    @Override
    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    // TODO 00 don't want this sig? Serialise on attach?
    public TransportEntry(QosRequestTransport transport, RequestContext requestContext, Receiver recv) {
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

    public TransportEntry(java.lang.String payload2, RequestTransport.TransportReceiver receiver2) {
        this.payload = payload2;
        this.receiver = receiver2;
    }


    @Override
    public void fire(QosRequestTransport transport) {
        // XXX cut n paste, move to super?
        setState("PENDING");
        transport.nextMode(new RequestTransport.TransportReceiver() {
            @Override
            public void onTransportSuccess(String payload) {
                setState("DONE");
                notifyChange();
            }

            @Override
            public void onTransportFailure(ServerFailure failure) {
                setState("FAIL");
                notifyChange();
            }
        });

        transport.send(payload, receiver);
    }

    @Override
    protected void reset() {
        state = null;
    }
}
