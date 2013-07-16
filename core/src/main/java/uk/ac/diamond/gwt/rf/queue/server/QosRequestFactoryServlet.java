/*
 * Diamond User Administration System
 * Copyright © 2013 Diamond Light Source Ltd
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
