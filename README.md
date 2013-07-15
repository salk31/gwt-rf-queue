gwt-rf-queue
============

How to use
----------
* `<inherits name='uk.ac.diamond.gwt.rf.queue.GwtRfQueue' />`
* Wire up the manager on the client:
<blockquote>
RequestFactory requests = ...
QosRequestTransport transport = new QosRequestTransport();
QosManager manager = new QosManager();

QosQueue root = new QosQueue();
root.setTarget(manager);
        
manager.start();
        
transport.setDefaultSource(primary);
requests.initialize(eventBus, transport);
</blockquote>

* Create your version of the RequestFactoryServlet (you probably have probably done this already)
<blockquote>
public class MyRequestFactoryServlet extends RequestFactoryServlet {
public UasRequestFactoryServlet() {
   QosSimpleRequestProcessor.decorate(this);
}


</blockquote>
* Refer to this servlet in your web.xml
