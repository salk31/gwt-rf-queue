package uk.ac.diamond.gwt.rf.queue.client.core;

public class FakeEntry extends QosEntry {
    private State state;

    private int firedCount;

    @Override
    protected void fire(QosRequestTransport transport) {
        firedCount++;
       // state = State.FAILED;
       // this.notifyChange();
    }

    @Override
    protected State getState() {
        return state;
    }

    @Override
    protected void reset() {
        state = null;
    }

    void setState(State p) {
        this.state = p;
        this.notifyChange();
    }

    public int getFiredCount() {
        return firedCount;
    }
}


