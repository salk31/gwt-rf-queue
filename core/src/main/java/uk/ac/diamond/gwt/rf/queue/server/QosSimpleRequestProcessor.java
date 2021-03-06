/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.server;

import java.lang.reflect.Field;

import uk.ac.diamond.gwt.rf.queue.shared.RequestFactoryQueue;

import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;

/**
 * EXPERT - Support batch requests.
 */
public class QosSimpleRequestProcessor extends SimpleRequestProcessor {

    public QosSimpleRequestProcessor(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    @Override
    public String process(String payload) {
        StringBuilder batchResponse = new StringBuilder();
        String[] split = payload.split(RequestFactoryQueue.DELIMITER);
        for (String payload2 : split) {
            if (batchResponse.length() > 0) {
                batchResponse.append(RequestFactoryQueue.DELIMITER);
            }
            batchResponse.append(super.process(payload2));
        }
        return batchResponse.toString();
    }

    // TODO 00 request hook to fix this
    public static void decorate(RequestFactoryServlet servlet) {
        try {
            Field processorField = RequestFactoryServlet.class.getDeclaredField("processor");
            processorField.setAccessible(true);

            SimpleRequestProcessor original = (SimpleRequestProcessor) processorField.get(servlet);
            Field serviceField = SimpleRequestProcessor.class.getDeclaredField("service");
            serviceField.setAccessible(true);

            ServiceLayer originalServiceLayer = (ServiceLayer) serviceField.get(original);

            QosSimpleRequestProcessor processor = new QosSimpleRequestProcessor(originalServiceLayer);
            processorField.set(servlet, processor);

            Field exceptionHandlerField = SimpleRequestProcessor.class.getDeclaredField("exceptionHandler");
            exceptionHandlerField.setAccessible(true);

            ExceptionHandler exceptionHandler = (ExceptionHandler) exceptionHandlerField.get(original);

            processor.setExceptionHandler(exceptionHandler);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }

    }
}
