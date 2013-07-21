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
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.diamond.gwt.rf.queue.shared.RequestFactoryQueue;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.requestfactory.gwt.client.DefaultRequestTransport;
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * EXPERT - A RequestTransport capable of batching semi independent FR Requests
 * across a single HTTP Request
 */
public class QosRequestTransport extends DefaultRequestTransport {

    private static final Logger wireLogger = Logger.getLogger("UasWireActivityLogger");

    /**
     * (Request) Payload and Receiver
     */
    static class BatchedRequest {
        String payload;
        RequestTransport.TransportReceiver receiver;
        RequestTransport.TransportReceiver treceiver;
    }

    private List<BatchedRequest> batch;

    private PipeInput defaultSource;

    private RequestTransport.TransportReceiver treceiver;

    private AuthFailureDetector authFailureDetector;

    public PipeInput getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(PipeInput defaultSource) {
        this.defaultSource = defaultSource;
    }

    void flush() {
        StringBuilder payload2 = new StringBuilder();

        for (BatchedRequest request : batch) {
            if (payload2.length() > 0) {
                payload2.append(RequestFactoryQueue.DELIMITER);
            }
            payload2.append(request.payload);
        }

        sendBatch(payload2.toString(), batch);

        batch = null;
    }

    void startBatch() {
        batch = new ArrayList<BatchedRequest>();
    }

    @Override
    public void send(java.lang.String payload, RequestTransport.TransportReceiver receiver) {
        if (treceiver != null) {

            BatchedRequest request = new BatchedRequest();
            request.payload = payload;
            request.receiver = receiver;
            request.treceiver = treceiver;

            if (batch != null) {
                batch.add(request);
            }

            if (batch == null) {
                batch = new ArrayList<BatchedRequest>();
                batch.add(request);
                flush();
            }

            treceiver = null;
        } else {
            defaultSource.add(new TransportEntry(payload, receiver));
        }
    }

    private void sendBatch(String payload, List<BatchedRequest> currentBatch) {
        // XXX use super.send?
        RequestBuilder builder = createRequestBuilder();
        configureRequestBuilder(builder);

        builder.setRequestData(payload);
        builder.setCallback(createRequestCallbackBatch(currentBatch));

        try {
            wireLogger.finest("Sending fire request");
            builder.send();
        } catch (RequestException e) {
            wireLogger.log(Level.SEVERE, " (" + e.getMessage()
             + ")", e);
        }
    }

    protected RequestCallback createRequestCallbackBatch(final List<BatchedRequest> currentBatch) {
        return new RequestCallback() {

            @Override
            public void onError(Request request, Throwable exception) {
                wireLogger.log(Level.WARNING, "onError.SERVER_ERROR", exception);
                for (BatchedRequest batchedRequest : currentBatch) {
                    batchedRequest.treceiver.onTransportFailure(new ServerFailure(exception.getMessage()));

                    try {
                        // XXX not do this till retry fails?
                        batchedRequest.receiver.onTransportFailure(new ServerFailure(exception.getMessage()));
                    } catch (Throwable th) {
                        wireLogger.log(Level.SEVERE, "RF Receiver failed", th);
                    }
                }
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                wireLogger.finest("Response received");
                if (authFailureDetector != null && authFailureDetector.isLoginRedirect(response)) {
                    for (BatchedRequest batchedRequest : currentBatch) {

                        // XXX only once per batch?
                        if (batchedRequest.treceiver != null) {
                            batchedRequest.treceiver.onTransportFailure(new ServerFailure("AUTH"));
                        }
                    }
                } else
                if (Response.SC_OK == response.getStatusCode()) {
                    String text = response.getText();

                    String[] split = text.split(RequestFactoryQueue.DELIMITER);
                    int i = 0;
                    for (String x : split) {

                        if (currentBatch.get(i).treceiver != null) {
                            currentBatch.get(i).treceiver.onTransportSuccess(x);
                        }
                        try {
                            currentBatch.get(i).receiver.onTransportSuccess(x);
                        } catch (Throwable th) {
                            // do not want these failures to leave Qos hanging
                            wireLogger.log(Level.SEVERE, "RF Receiver failed", th);
                        }
                        // XXX only once per batch?

                        i++;
                    }
                } else {
                    String message = "onResponseReceived.SERVER_ERROR" + " " + response.getStatusCode() + " " + response.getText();
                    wireLogger.warning(message);
                    for (BatchedRequest batchedRequest : currentBatch) {
                        batchedRequest.treceiver.onTransportFailure(new ServerFailure(message));
                    }
                }
            }
        };
    }

    /**
     * Ensure the batch is closed. XXX Fail if not?
     */
    public List<BatchedRequest> clear() {
        List<BatchedRequest> orig = batch;
        batch = null;
        return orig;
    }


    public void nextMode(RequestTransport.TransportReceiver treceiver2) {
        treceiver = treceiver2;
    }

    public AuthFailureDetector getAuthFailureDetector() {
        return authFailureDetector;
    }

    public void setAuthFailureDetector(AuthFailureDetector p) {
        this.authFailureDetector = p;
    }


}
