/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.dynatablerf.client;


import java.util.logging.Logger;

import uk.ac.diamond.gwt.rf.queue.client.core.AuthFailureDetector;
import uk.ac.diamond.gwt.rf.queue.client.core.QosEvent;
import uk.ac.diamond.gwt.rf.queue.client.core.QosEventHandler;
import uk.ac.diamond.gwt.rf.queue.client.core.QosManager;
import uk.ac.diamond.gwt.rf.queue.client.core.QosQueue;
import uk.ac.diamond.gwt.rf.queue.client.core.QosRequestTransport;
import uk.ac.diamond.gwt.rf.queue.client.ui.SimpleNotification;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Response;
import com.google.gwt.sample.dynatablerf.client.widgets.DayFilterWidget;
import com.google.gwt.sample.dynatablerf.client.widgets.FavoritesWidget;
import com.google.gwt.sample.dynatablerf.client.widgets.SummaryWidget;
import com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The entry point class which performs the initial loading of the DynaTableRf
 * application.
 */
public class DynaTableRf implements EntryPoint {
  interface Binder extends UiBinder<Widget, DynaTableRf> {
  }

  private static final Logger log = Logger.getLogger(DynaTableRf.class.getName());

  @UiField(provided = true)
  SummaryWidget calendar;

  EventBus eventBus = new SimpleEventBus();

  @UiField(provided = true)
  FavoritesWidget favorites;

  @UiField(provided = true)
  DayFilterWidget filter;

  @UiField
  Label queueLength;

  @UiField
  Label authStatus;

  @UiField
  Label retryCount;

  /**
   * This method sets up the top-level services used by the application.
   */
  @Override
  public void onModuleLoad() {

    Cookies.removeCookie("authOff");
    Cookies.removeCookie("networkOff");

    final DynaTableRequestFactory requests = GWT.create(DynaTableRequestFactory.class);


    QosRequestTransport transport = new QosRequestTransport();
    QosManager manager = new QosManager();
    manager.setRequestTransport(transport);

    QosQueue root = new QosQueue();
    root.setTarget(manager);
    manager.start();

    transport.setDefaultSource(root);
    requests.initialize(eventBus, transport);

    SimpleNotification sn = new SimpleNotification(manager);


    manager.addQosEventHandler(new QosEventHandler() {

    @Override
    public void onQosEvent(QosEvent qosEvent) {
        queueLength.setText("" + qosEvent.getList().size());

        if (qosEvent.getRetryCount() > 0) {
            retryCount.setText("" + qosEvent.getRetryCount());
        } else {
            retryCount.setText("-");
        }
    }
    });

    transport.setAuthFailureDetector(new AuthFailureDetector() {
        @Override
        public boolean isLoginRedirect(Response response) {
            // works for things like CAS with spring integration.
            String location = response.getHeader("Location");
            boolean authFail =  location != null && !location.isEmpty();

            if (authFail) {
                authStatus.setText("Failed");
            } else {
                authStatus.setText("OK");
            }
            return authFail;
        }
    });


    // Add remote logging handler
//    RequestFactoryLogHandler.LoggingRequestProvider provider = new RequestFactoryLogHandler.LoggingRequestProvider() {
//      @Override
//    public LoggingRequest getLoggingRequest() {
//        return requests.loggingRequest();
//      }
//    };
//    Logger.getLogger("").addHandler(new ErrorDialog().getHandler());
//    Logger.getLogger("").addHandler(
//        new RequestFactoryLogHandler(provider, Level.WARNING,
//            new ArrayList<String>()));
    FavoritesManager manager2 = new FavoritesManager(requests);
    PersonEditorWorkflow.register(eventBus, requests, manager2);

    calendar = new SummaryWidget(eventBus, requests, 15);
    favorites = new FavoritesWidget(eventBus, requests, manager2);
    filter = new DayFilterWidget(eventBus);

    RootLayoutPanel.get().add(
        GWT.<Binder> create(Binder.class).createAndBindUi(this));

    // Fast test to see if the sample is not being run from devmode
//    if (GWT.getHostPageBaseURL().startsWith("file:")) {
//      log.log(Level.SEVERE, "The DynaTableRf sample cannot be run without its"
//          + " server component.  If you are running the sample from a"
//          + " GWT distribution, use the 'ant devmode' target to launch"
//          + " the DTRF server.");
//    }
  }

  @UiHandler("toggleNetwork")
  void doToggleNetwork(ValueChangeEvent<Boolean> event) {
    toggleCookie("networkOff", event.getValue());
  }

  @UiHandler("toggleAuth")
  void doToggleAuth(ValueChangeEvent<Boolean> event) {
    toggleCookie("authOff", event.getValue());
  }

  private void toggleCookie(String name, boolean active) {
    if (active) {
      Cookies.setCookie(name, "true");
    } else {
      Cookies.removeCookie(name);
    }
  }
}
