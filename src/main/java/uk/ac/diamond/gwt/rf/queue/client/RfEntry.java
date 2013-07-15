package uk.ac.diamond.gwt.rf.queue.client;


import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 *
 *
 * @author yjs77802
 */
class RfEntry extends QosEntry {
    private Receiver receiver;

    private RequestContext requestContext;

    private String state;

    @Override
    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    public RfEntry(Request request) {
        this(request.getRequestContext());
    }

    public RfEntry(Request request, Receiver receiver2) {
        this(request.getRequestContext(), receiver2);
    }

    public RfEntry(RequestContext requestContext2) {
        this(requestContext2, null);
    }

    public RfEntry(RequestContext requestContext2, Receiver receiver2) {
        this.requestContext = requestContext2;
        this.receiver = receiver2;
    }

    RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public void fire(QosRequestTransport transport) {
        setState("PENDING");
        transport.nextMode(new RequestTransport.TransportReceiver() {
            @Override
            public void onTransportSuccess(String payload) {
                setState("DONE");
                notifyChange();
            }

            @Override
            public void onTransportFailure(ServerFailure failure) {
                setState("FAIL");
                notifyChange();
            }
        });

        if (receiver == null) {
            requestContext.fire();
        } else {
            requestContext.fire(receiver);
        }
    }

    @Override
    protected void reset() {
        state = null;
    }
}
