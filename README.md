gwt-rf-queue
============

Simple demo http://gwt-rf-queue.appspot.com/ with gwt-rf-queue retrofitted to DynaTableRf.

Features
--------
Pure GWT/Java RequestFactory module for:
* GMail like network retry.
* Detect/retry auth failure.
* Can drive the UI to indicate the RequestFactory is busy or idle.
* Different "quality of service" for different requests.
* Mechanism to send the contents of an Editor tree to the server multiple times and fire different service methods. e.g. to support background validation on the server


Roadmap
-------
* TODO - Semi-respectable unit test coverage.
* TODO - Raise GWT feature requests to add hooks to remove the worst work arounds e.g. custom Processor.
* TODO - Rename AuthFailureDetector to something more generic about "response detector"?
* TODO - Put in maven central.


How to use
----------
1) Inherit the module in your gwt.xml file:
```xml
<inherits name='uk.ac.diamond.gwt.rf.queue.GwtRfQueue' />
```

2) Wire up the manager on the client (very minimal, will just do network retry, no UI):

```java
  RequestFactory requestFactory = ...
  QosRequestTransport transport = new QosRequestTransport();
  QosManager manager = new QosManager();
  manager.setRequestTransport(transport);
  QosQueue root = new QosQueue();
  root.setTarget(manager);
  manager.start();
  
  transport.setDefaultSource(root);
  requestFactory.initialize(eventBus, transport);
```

3) Reference the decorated servlet in your web.xml file:

```xml
    <servlet>
        <servlet-name>requestFactoryServlet</servlet-name>
        <servlet-class>uk.ac.diamond.gwt.rf.queue.server.QosRequestFactoryServlet</servlet-class>
    </servlet>
```

