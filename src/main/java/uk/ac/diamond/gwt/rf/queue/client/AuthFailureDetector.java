package uk.ac.diamond.gwt.rf.queue.client;

import com.google.gwt.http.client.Response;

/**
 *
 */
public interface AuthFailureDetector {
    boolean isLoginRedirect(Response response);
}
