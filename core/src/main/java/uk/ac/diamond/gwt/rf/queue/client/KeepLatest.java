package uk.ac.diamond.gwt.rf.queue.client;

/**
 * Discard all but the latest entry.
 */
public class KeepLatest extends QosModifier {
    private QosEntry latest;

    @Override
    void add(QosEntry e) {
        this.latest = e;
    }

    @Override
    float getScore(QosEntry e) {
        if (e == latest) {
            return 1.0f;
        }
        return 0.0f;
    }
}
