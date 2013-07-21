package uk.ac.diamond.gwt.rf.queue.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface QosResources extends ClientBundle {
    QosResources INSTANCE = GWT.create(QosResources.class);

    @Source("qos.css")
    QosCss css();
}
