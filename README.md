gwt-rf-queue
============

How to use
----------
Inherit the module in your gwt.xml file:
```xml
<inherits name='uk.ac.diamond.gwt.rf.queue.GwtRfQueue' />
```

Wire up the manager on the client:

```java
  RequestFactory requestFactory = ...
  QosRequestTransport transport = new QosRequestTransport();
  QosManager manager = new QosManager();
  QosQueue root = new QosQueue();
  root.setTarget(manager);
  manager.start();
  
  transport.setDefaultSource(root);
  requestFactory.initialize(eventBus, transport);
```

* Reference the decorated servlet in your web.xml file:

```xml
    <servlet>
        <servlet-name>requestFactoryServlet</servlet-name>
        <servlet-class>uk.ac.diamond.gwt.rf.queue.server.QosRequestFactoryServlet</servlet-class>
    </servlet>
```

