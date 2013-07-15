package uk.ac.diamond.gwt.rf.queue.client;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;

/**
 * Accept entries to be prioritised.
 */
public abstract class AbstractPipeInput implements PipeInput {

    @Override
    public void add(Request r) {
        add(new RfEntry(r));
    }

    @Override
    public void add(RequestContext r) {
        add(new RfEntry(r));
    }

    @Override
    public void add(RequestContext r, Receiver recv) {
        add(new RfEntry(r, recv));
    }
}
