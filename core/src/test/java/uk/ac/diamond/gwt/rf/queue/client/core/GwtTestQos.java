package uk.ac.diamond.gwt.rf.queue.client.core;

import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.requestfactory.shared.testing.FakeRequestContext;

/**
 * Test Qos.
 */
public class GwtTestQos extends GWTTestCase {

    private final FakeHandler handler = new FakeHandler();

    private final QosManager manager = new QosManager();

    QosQueue pipe = new QosQueue();


    @Override
    public void gwtSetUp() {
        pipe.setTarget(manager);

        manager.addQosEventHandler(handler);
    }

    public void testForEditorUsage() throws Exception {

        QosManager manager = new QosManager();

        QosQueue qForEditor = new QosQueue();
        qForEditor.setTarget(manager);

        QosQueue qForForeground = new QosQueue(); // XXX want to set don't
        qForForeground.setTarget(qForEditor);

        QosQueue qForBackground = new QosQueue();
        qForBackground.addModifier(new KeepLatest());
        qForBackground.addModifier(new Priority(0.1f));
        qForBackground.setTarget(qForEditor);

        FakeRequestContext userClick = new FakeRequestContext();
        qForForeground.add(userClick);

        FakeRequestContext validation0 = new FakeRequestContext();
        qForBackground.add(validation0);
        FakeRequestContext validation1 = new FakeRequestContext();
        qForBackground.add(validation1);

        {
            List<QosEntry> list = manager.getList();

            assertEquals(2, list.size());
            assertTrue(userClick == ((RfEntry) list.get(0)).getRequestContext());
            assertTrue(validation1 == ((RfEntry) list.get(1)).getRequestContext());
        }

        NonAtomicBatch batch = new NonAtomicBatch();
        batch.add(new FakeRequestContext());
        batch.add(new FakeRequestContext());
        qForForeground.add(batch);

        {
            List<QosEntry> list = manager.getList();

            assertEquals(3, list.size());
            assertTrue(batch == list.get(1));
        }
    }


    public void testDelayOnTransportFailure3() throws Exception {
        FakeEntry fakeEntry = new FakeEntry();

        pipe.add(fakeEntry);

        manager.tick();
        assertEquals(1, fakeEntry.getFiredCount());

        fakeEntry.setState(QosEntry.State.FAILED);

        manager.tick();
        assertEquals(1, fakeEntry.getFiredCount());

        manager.tock();
        assertEquals(2, fakeEntry.getFiredCount());
        // should be there marked FAIL but not execute again

    }

    public void testClearRetryOnTick() {
        FakeEntry e0 = new FakeEntry();
        pipe.add(e0);

        e0.setState(QosEntry.State.FAILED);
        manager.tock();

        assertEquals(1, handler.getLast().getRetryCount());

        e0.setState(QosEntry.State.DONE);

        assertEquals(0, handler.getLast().getRetryCount());
    }

    public void testRetryNotClearedIfAllPending() {
        FakeEntry fakeEntry = new FakeEntry();
        pipe.add(fakeEntry);

        fakeEntry.setState(QosEntry.State.FAILED);

        manager.tock();

        fakeEntry.setState(QosEntry.State.PENDING);

        manager.tock();

        assertEquals(1, handler.getLast().getRetryCount());

    }

    @Override
    public String getModuleName() {
        return "uk.ac.diamond.gwt.rf.queue.GwtRfQueue";
    }

}
