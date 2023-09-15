package com.salesforce.impl;

import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SfdcConnector {
    private static ConnectorConfig getConnectorConfig() {
        ConnectorConfig credentials = new ConnectorConfig();
        credentials.setUsername("ss@demo.com");
        credentials.setPassword("test1234567");
        return credentials;
    }

    public static EnterpriseConnection getEnterpriseConnection() throws ConnectionException {
        ConnectorConfig credentials = SfdcConnector.getConnectorConfig();
        return Connector.newConnection(credentials);
    }
}
