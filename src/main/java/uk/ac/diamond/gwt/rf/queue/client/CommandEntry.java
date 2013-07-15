package uk.ac.diamond.gwt.rf.queue.client;


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * An entry that executes arbitrary code.
 */
public abstract class CommandEntry extends QosEntry implements Command {

    private String state;

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void fire(QosRequestTransport transport) {
        state = "PENDING"; // XXX MAGIC
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                CommandEntry.this.execute();
                state = "DONE"; // XXX magic
            }

        });
    }

    @Override
    protected void reset() {
        state = null;
    }
}
