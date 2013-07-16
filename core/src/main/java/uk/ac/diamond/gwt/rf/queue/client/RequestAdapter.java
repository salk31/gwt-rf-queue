package uk.ac.diamond.gwt.rf.queue.client;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;

/**
 * Allow a Qos entry to be triggered and altered via a Request.
 *
 * XXX EXPERIMENTAL - Would be nice not to need this
 */
public class RequestAdapter implements Request {

    private final PipeInput out;

    private final QosEntry entry;

    private final Request request;

    public RequestAdapter(PipeInput out2, QosEntry entry2, Request request2) {
        this.out = out2;
        this.entry = entry2;
        this.request = request2;
    }

    @Override
    public void fire() {
        out.add(entry);
    }

    @Override
    public void fire(Receiver receiver) {
        to(receiver);
        fire();
    }

    @Override
    public RequestContext getRequestContext() {
        return request.getRequestContext();
    }

    @Override
    public RequestContext to(Receiver receiver) {
        request.to(receiver);
        return getRequestContext();
    }

    @Override
    public Request with(String... propertyRefs) {
        return request.with(propertyRefs);
    }

}