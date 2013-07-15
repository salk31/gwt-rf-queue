package uk.ac.diamond.gwt.rf.queue.client;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;


/**
 * Batch together multiple requests. Unlike RequestBatcher each one acts independently.
 */
public class NonAtomicBatch extends QosEntry implements PipeInput {
    private List<QosEntry> list = new ArrayList<QosEntry>();

    // XXX __ not done until all done?
    @Override
    public String getState() {
        String state = null;
        for (QosEntry f : list) {
            if (f.getState() != null) {
                state = f.getState();
            }
        }
        return state;
    }


    @Override
    public void fire(QosRequestTransport transport) {
        transport.startBatch();
        for (QosEntry f : list) {
            f.fire(transport);
        }
        transport.flush();
    }

    @Override
    public void add(QosEntry foo) {
        list.add(foo);
    }

    @Override
    public void add(Request r) {
        add(new RfEntry(r));
    }

    public void add(Request r, Receiver recv) {
        add(new RfEntry(r, recv));
    }

    @Override
    public void add(RequestContext r) {
        add(new RfEntry(r));
    }

    @Override
    public void add(RequestContext r, Receiver recv) {
        add(new RfEntry(r, recv));
    }

    @Override
    protected void attachToManager(QosManager manager) {
        super.attachToManager(manager);
        for (QosEntry child : list) {
            // XXX only want a tick when batch completed?
            child.attachToManager(manager);
        }
    }

    @Override
    protected void reset() {
        for (QosEntry f : list) {
            f.reset();
        }
    }
}
