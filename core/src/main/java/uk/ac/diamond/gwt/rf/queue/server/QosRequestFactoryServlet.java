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

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * Off the shelf QOS RequestFactoryServlet.
 *
 * Presumably most users will create their own.
 */
public class QosRequestFactoryServlet extends RequestFactoryServlet {
    public QosRequestFactoryServlet() {
        QosSimpleRequestProcessor.decorate(this);
    }
}
