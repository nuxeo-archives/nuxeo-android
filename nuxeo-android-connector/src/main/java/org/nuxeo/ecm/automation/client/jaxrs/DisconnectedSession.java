package org.nuxeo.ecm.automation.client.jaxrs;

import org.nuxeo.ecm.automation.client.jaxrs.spi.AbstractAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;

public class DisconnectedSession extends DefaultSession implements Session {

    public DisconnectedSession(AbstractAutomationClient client,
            Connector connector, LoginInfo login) {
        super(client, connector, login);
    }

    @Override
    public Object execute(OperationRequest request) throws Exception {
        request.forceCache();
        return super.execute(request);
    }

    @Override
    public boolean isOffline() {
        return true;
    }

}
