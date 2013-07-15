package uk.ac.diamond.gwt.rf.queue.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.requestfactory.shared.testing.FakeRequestContext;

/**
 * Test Qos.
 */
public class GwtTestQos extends GWTTestCase {



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

        final QosManager manager = new QosManager();

        QosQueue pipe = new QosQueue();
        pipe.setTarget(manager);

        final List<Object> fired = new ArrayList<Object>();

        QosEntry fakeEntry = new QosEntry() {
            String state;
            @Override
            protected void fire(QosRequestTransport transport) {
                fired.add("1");
                state = "FAIL";
                this.notifyChange();
            }

            @Override
            protected String getState() {
                return state;
            }

            @Override
            protected void reset() {
                state = null;
            }
        };

        pipe.add(fakeEntry);

        manager.tick();
        assertEquals(1, fired.size());

        manager.tick();
        assertEquals(1, fired.size());

        manager.retry();
        manager.tick();
        assertEquals(2, fired.size());
        // should be there marked FAIL but not execute again

    }

    @Override
    public String getModuleName() {
        return "uk.ac.diamond.gwt.rf.queue.GwtRfQueue";
    }

}
