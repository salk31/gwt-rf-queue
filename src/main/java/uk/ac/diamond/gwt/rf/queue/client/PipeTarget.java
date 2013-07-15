package uk.ac.diamond.gwt.rf.queue.client;

/**
 * Can be used as the target for a QosEntry.
 */
public interface PipeTarget {
    void add(QosEntry e);
}
