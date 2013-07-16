package uk.ac.diamond.gwt.rf.queue.client;

import java.util.List;

/**
 * QOS system listener for changes.
 */
public interface QosListener {
    void tick(List<QosEntry> list);

    /**
     * Transport error (so network or RequestFactory Transport error).
     */
    void retryStarting(int retryCount);

    void retryEnding();
}
