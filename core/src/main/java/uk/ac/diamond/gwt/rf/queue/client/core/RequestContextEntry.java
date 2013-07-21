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

// TODO 00 unit test this being re-fired on each retry
/**
 * RequestFactory QosEntry.
 */
class RequestContextEntry extends QosEntryWithState {
    private final RequestContext requestContext;

    private final Receiver receiver;


    public RequestContextEntry(Request request) {
        this(request.getRequestContext());
    }

    public RequestContextEntry(Request request, Receiver receiver2) {
        this(request.getRequestContext(), receiver2);
    }

    public RequestContextEntry(RequestContext requestContext2) {
        this(requestContext2, null);
    }

    public RequestContextEntry(RequestContext requestContext2, Receiver receiver2) {
        this.requestContext = requestContext2;
        this.receiver = receiver2;
    }

    RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public void fire(QosRequestTransport transport) {
        setState(State.PENDING);
        transport.setNextReceiverForEntry(this);

        if (receiver == null) {
            requestContext.fire();
        } else {
            requestContext.fire(receiver);
        }
    }
}
