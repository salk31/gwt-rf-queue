package uk.ac.diamond.gwt.rf.queue.client;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;

/**
 * Add more convenient ways to add to a Pipe.
 *
 * XXX enum for state
 *
 */
public interface PipeInput extends PipeTarget {

    void add(Request r);

    void add(RequestContext r);

    void add(RequestContext r, Receiver recv);

}
