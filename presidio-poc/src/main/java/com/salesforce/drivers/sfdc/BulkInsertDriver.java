package com.salesforce.drivers.sfdc;

import com.salesforce.impl.bulkapi.BulkJobExecutor;
import com.salesforce.impl.SfdcConnector;
import com.sforce.async.*;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class BulkInsertDriver {
    public static void main (String[] args) {
        long startTime = System.currentTimeMillis();

        String sObjectType = "Lead";
        int NUM_RECORDS = 10;
        int BATCH_LIMIT = 10000;

        try {
            ConnectorConfig enterpriseConfig = SfdcConnector.getEnterpriseConnection().getConfig();
            new EnterpriseConnection(enterpriseConfig);
            BulkJobExecutor bulkJobExecutor = new BulkJobExecutor(enterpriseConfig, sObjectType, new InsertBatchCreator());
            bulkJobExecutor.execute(NUM_RECORDS, BATCH_LIMIT);
        } catch (AsyncApiException | ConnectionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken (milliseconds) = " + (endTime - startTime));
        }
    }
}
