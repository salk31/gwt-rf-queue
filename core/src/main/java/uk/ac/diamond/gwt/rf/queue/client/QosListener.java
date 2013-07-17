/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package uk.ac.diamond.gwt.rf.queue.client;

import java.util.List;

/**
 * QOS system listener for changes.
 */
public interface QosListener {
    void tick(List<QosEntry> list);

    /**
     * Transport error (so network or RequestFactory Transport error).
     */
    void retryStarting(int retryCount);

    void retryEnding();
}
