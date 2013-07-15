package uk.ac.diamond.gwt.rf.queue.client;

/**
 * Set the priority given to a QosEntry.
 *
 * Higher values are prioritised over lower values.
 */
public class Priority extends QosModifier {
    private float score;

    public Priority(float score2) {
        this.score = score2;
    }

    @Override
    float getScore(QosEntry e) {
        return score;
    }
}
