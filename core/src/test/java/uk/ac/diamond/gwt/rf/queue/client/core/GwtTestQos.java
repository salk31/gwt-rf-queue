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
            assertTrue(userClick == ((RequestContextEntry) list.get(0)).getRequestContext());
            assertTrue(validation1 == ((RequestContextEntry) list.get(1)).getRequestContext());
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
    
    public void testDelayOnTransportFailure() throws Exception {
        FakeEntry fakeEntry = new FakeEntry();

        pipe.add(fakeEntry);

        manager.fireReady();
        assertEquals(1, fakeEntry.getFiredCount());

        fakeEntry.setState(QosEntry.State.FAILED);

        manager.fireReady();
        assertEquals(1, fakeEntry.getFiredCount());

        manager.tick();
        assertEquals(2, fakeEntry.getFiredCount());
        // should be there marked FAIL but not execute again

    }

    public void testClearRetryImmediatelyOnStateChange() {
        FakeEntry e0 = new FakeEntry();
        pipe.add(e0);

        e0.setState(QosEntry.State.FAILED);
        manager.tick();

        assertEquals(1, handler.getLast().getRetryCount());

        e0.setState(QosEntry.State.DONE);

        assertEquals(0, handler.getLast().getRetryCount());
    }

    public void testRetryNotClearedIfAllPending() {
        FakeEntry fakeEntry = new FakeEntry();
        pipe.add(fakeEntry);

        fakeEntry.setState(QosEntry.State.FAILED);

        manager.tick();

        fakeEntry.setState(QosEntry.State.PENDING);

        manager.tick();

        assertEquals(1, handler.getLast().getRetryCount());

    }

    public void testBackOff() throws Exception {
      FakeEntry fakeEntry = new FakeEntry();

      pipe.add(fakeEntry);
      manager.fireReady();  // make sync
      
      assertEquals(1, fakeEntry.getFiredCount());
      fakeEntry.setState(QosEntry.State.FAILED);

      manager.tick(); // 1st retry 1
      assertEquals(2, fakeEntry.getFiredCount());
      fakeEntry.setState(QosEntry.State.FAILED);

      manager.tick(); // 2nd retry 1/2
      assertEquals(2, fakeEntry.getFiredCount());

      manager.tick(); // 2nd retry 2/2
      assertEquals(3, fakeEntry.getFiredCount());
      fakeEntry.setState(QosEntry.State.FAILED);

      manager.tick(); // 3rd retry 1/4
      assertEquals(3, fakeEntry.getFiredCount());
      
      manager.tick(); // 3rd retry 2/4
      assertEquals(3, fakeEntry.getFiredCount());
      
      manager.tick(); // 3rd retry 3/4
      assertEquals(3, fakeEntry.getFiredCount());
      
      manager.tick(); // 3rd retry 4/4
      assertEquals(4, fakeEntry.getFiredCount());
      fakeEntry.setState(QosEntry.State.FAILED);
      
    
  }
    
    @Override
    public String getModuleName() {
        return "uk.ac.diamond.gwt.rf.queue.GwtRfQueue";
    }

}
