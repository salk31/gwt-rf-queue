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

import com.google.gwt.event.shared.EventHandler;

/**
 * QOS system listener for changes.
 */
public interface QosEventHandler extends EventHandler {
    void onQosEvent(QosEvent qosEvent);
}
