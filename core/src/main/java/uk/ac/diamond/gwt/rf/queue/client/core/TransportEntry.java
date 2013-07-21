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
public class TransportEntry extends QosEntryWithState {

    private final String payload;

    private final RequestTransport.TransportReceiver receiver;



    // TODO 00 unit test can create lots of these, fire when feel like it
    public TransportEntry(RequestContext requestContext, Receiver recv) {
        QosRequestTransport transport = (QosRequestTransport) requestContext.getRequestFactory().getRequestTransport();
        transport.startBatch();

        transport.setNextReceiverForEntry(QosRequestTransport.CAPTURE);

        if (recv == null) {
            requestContext.fire();
        } else {
            requestContext.fire(recv);
        }

        List<BatchedRequest> b = transport.flushBatch();
        payload = b.get(0).payload;
        receiver = b.get(0).receiver;

        // close the RequestContext so can be re-used.
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
        setState(State.PENDING);
        transport.setNextReceiverForEntry(this);

        transport.send(payload, receiver);
    }
}
