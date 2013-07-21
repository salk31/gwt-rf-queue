package uk.ac.diamond.gwt.rf.queue.client.core;

import com.google.web.bindery.requestfactory.shared.RequestTransport;
import com.google.web.bindery.requestfactory.shared.ServerFailure;


public abstract class QosEntryWithState extends QosEntry implements RequestTransport.TransportReceiver {





    private State state;

    @Override
    public State getState() {
        return state;
    }

    void setState(State p) {
        this.state = p;
    }

    @Override
    protected void reset() {
        setState(null);
    }

    @Override
    public void onTransportSuccess(String payload) {
        setState(State.DONE);
        notifyChange();
    }

    @Override
    public void onTransportFailure(ServerFailure failure) {
        setState(State.FAILED);
        notifyChange();
    }
}
