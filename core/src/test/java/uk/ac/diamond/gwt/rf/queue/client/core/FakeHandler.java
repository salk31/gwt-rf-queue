package uk.ac.diamond.gwt.rf.queue.client.core;

import java.util.ArrayList;
import java.util.List;

public class FakeHandler implements QosEventHandler {

    private final List<QosEvent> list = new ArrayList<QosEvent>();

    @Override
    public void onQosEvent(QosEvent qosEvent) {
        list.add(qosEvent);
    }

    public QosEvent getLast() {
        return list.get(list.size() - 1);
    }

    public int getSize() {
        return list.size();
    }

}
