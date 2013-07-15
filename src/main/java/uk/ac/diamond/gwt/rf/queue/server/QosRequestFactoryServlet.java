package uk.ac.diamond.gwt.rf.queue.server;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * Pre-built Servlet decorated with special processor.
 */
public class QosRequestFactoryServlet extends RequestFactoryServlet {
    public QosRequestFactoryServlet() {
        QosSimpleRequestProcessor.decorate(this);
    }
}
