package uk.ac.diamond.gwt.rf.queue.client;

/**
 * Alter the QOS contract in some way.
 *
 */
public class QosModifier {
    void add(QosEntry e) {
    }

    float getScore(QosEntry e) {
        return 1.0f;
    }

    protected boolean isBlocked(QosEntry e) {
        return false;
    }
}
