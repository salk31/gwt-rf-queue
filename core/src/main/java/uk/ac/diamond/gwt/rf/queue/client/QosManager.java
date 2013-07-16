package uk.ac.diamond.gwt.rf.queue.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;

/**
 * Manage the pipe.
 */
public class QosManager implements PipeTarget {

    private final List<QosEntry> list = new ArrayList<QosEntry>();

    private final List<QosListener> listeners = new ArrayList<QosListener>();

    private int retryCount;

    private QosRequestTransport requestTransport;

    public QosManager() {
    }

    public void start() {
        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
            @Override
            public boolean execute() {
                retry();
                tick();
                return true;
            }
        }, 2000);
    }

    void preProcess() {
        Iterator<QosEntry> it = list.iterator();
        while (it.hasNext()) {
            QosEntry e = it.next();

            if (e.isReady()) {
                float score = 1.0f;
                for (QosModifier m : e.getModifiers()) {
                    score = score * m.getScore(e);
                    if (score == 0.f) {
                        it.remove();
                        break;
                    }
                }
                e.setScore(score);
            }
        }
        Collections.sort(list);
    }

    boolean isBlocked(QosEntry e) {
        for (QosModifier m : e.getModifiers()) {
            if (m.isBlocked(e)) {
                return true;
            }
        }
        return false;
    }

    void tick() {
        Iterator<QosEntry> it = getList().iterator();
        while (it.hasNext()) {
            QosEntry entry = it.next();

            if (entry.isReady()) {
                if (!isBlocked(entry)) {
                    entry.fire(requestTransport);
                }
            }
            // XXX magic
            if ("DONE".equals(entry.getState())) {
                it.remove();
            }
        }

        Document.get().getDocumentElement().setPropertyString("qosPending", "" + list.size());
        for (QosListener listener : listeners) {
            listener.tick(list);
        }
    }


    @Override
    public void add(QosEntry e) {
        e.attachToManager(this);
        for (QosModifier m : e.getModifiers()) {
            m.add(e);
        }
        list.add(e);
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                tick();
            }
        });
    }

    List<QosEntry> getList() {
        preProcess();
        return list;
    }

    public void retry() {
        boolean backOff = false;
        for (QosEntry q : list) {
            if ("FAIL".equals(q.getState())) {
                q.reset();
                backOff = true;
            }
        }
        if (backOff) {
            retryCount++;
            for (QosListener listener : listeners) {
                listener.retryStarting(retryCount);
            }
        } else {
            if (retryCount > 0) {
                for (QosListener listener : listeners) {
                    listener.retryEnding();
                }
            }
            retryCount = 0;
        }
    }

    public void addListener(QosListener qosListener) {
        listeners.add(qosListener);
    }

    public QosRequestTransport getRequestTransport() {
        return requestTransport;
    }

    public void setRequestTransport(QosRequestTransport p) {
        this.requestTransport = p;
    }


}
