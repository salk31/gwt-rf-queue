package uk.ac.diamond.gwt.rf.queue.server;

import uk.ac.diamond.gwt.rf.queue.shared.RequestFactoryQueue;

import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;

/**
 * EXPERT - Support batch requests.
 */
public class UasSimpleRequestProcessor extends SimpleRequestProcessor {

    public UasSimpleRequestProcessor(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    @Override
    public String process(String payload) {
        StringBuilder batchResponse = new StringBuilder();
        String[] split = payload.split(RequestFactoryQueue.DELIMITER);
        for (String payload2 : split) {
            if (batchResponse.length() > 0) {
                batchResponse.append(RequestFactoryQueue.DELIMITER);
            }
            batchResponse.append(super.process(payload2));
        }
        return batchResponse.toString();
    }
}
