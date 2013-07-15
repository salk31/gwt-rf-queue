package uk.ac.diamond.gwt.rf.queue.client;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple queue of QosEntries.
 */
public class QosQueue extends AbstractPipeInput {
    private PipeTarget target;

    private List<QosModifier> modifiers = new ArrayList<QosModifier>();

    @Override
    public void add(QosEntry e) {
        e.addAll(modifiers);
        target.add(e);
    }

    public void setTarget(PipeTarget p) {
        this.target = p;
    }

    public void addModifier(QosModifier m) {
        modifiers.add(m);
    }
}
