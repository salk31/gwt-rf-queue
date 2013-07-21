/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.client.core;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * Event fired after each 'click' of the QosManager.
 *
 */
public class QosEvent extends GwtEvent<QosEventHandler>{

    /**
     * Handler type.
     */
    private static Type<QosEventHandler> TYPE;

    private final List<QosEntry> list;

    private final int retryCount;

    private final int retryPeriod;

    public QosEvent(List<QosEntry> list2, int retryCount2, int retryPeriod2) {
        this.list = list2;
        this.retryCount = retryCount2;
        this.retryPeriod = retryPeriod2;
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<QosEventHandler> getType() {
      if (TYPE == null) {
        TYPE = new Type<QosEventHandler>();
      }
      return TYPE;
    }


    @Override
    public GwtEvent.Type<QosEventHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(QosEventHandler handler) {
      handler.onQosEvent(this);
    }

    public List<QosEntry> getList() {
        return list;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRetryPeriod() {
        return retryPeriod;
    }
}
