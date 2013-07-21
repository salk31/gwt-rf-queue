/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.client.ui;

import uk.ac.diamond.gwt.rf.queue.client.core.QosEvent;
import uk.ac.diamond.gwt.rf.queue.client.core.QosEventHandler;
import uk.ac.diamond.gwt.rf.queue.client.core.QosManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Very simple PopupPanel based notification.
 */
public class SimpleNotification implements QosEventHandler {
    static {
        QosResources.INSTANCE.css().ensureInjected();
    }

    private final PopupPanel popup = new PopupPanel();

    private final InlineLabel message = new InlineLabel();

    private final Anchor retryWidget = new Anchor();

    private final QosManager manager;

    public SimpleNotification(QosManager manager2) {
        this.manager = manager2;

        manager.addQosEventHandler(this);

        FlowPanel container = new FlowPanel();
        container.add(message);
        container.add(retryWidget);
        popup.add(container);

        retryWidget.setText("Try now");
        retryWidget.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                manager.retryNow();
            }
        });

        popup.setStyleName(QosResources.INSTANCE.css().simpleNotification());
    }

    @Override
    public void onQosEvent(QosEvent qosEvent) {
        if (qosEvent.getList().size() > 0) {
            popup.show();
            if (qosEvent.getRetryCount() > 0) {
                retryWidget.setVisible(true);
                message.setText("Retrying in " + msToHuman(qosEvent.getRetryPeriod()) + "... ");
            } else {
                retryWidget.setVisible(false);
                message.setText("Loading...");
            }
            position();
        } else {
            popup.hide();
        }
    }

    private static String msToHuman(int ms) {
        int sec = ms / 1000;
        if (sec < 60) {
            return sec + " seconds";
        }
        int min = sec / 60;
        if (min < 60) {
            return min + " minutes";
        }
        int hour = min / 60;
        return hour + " hours";
    }

    private void position() {
        popup.setPopupPosition((Window.getClientWidth() - popup.getOffsetWidth()) / 2, 30);
    }
}
