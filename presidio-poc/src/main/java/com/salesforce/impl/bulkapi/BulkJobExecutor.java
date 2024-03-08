package com.salesforce.impl.bulkapi;

import com.salesforce.impl.Constants;
import com.salesforce.impl.bulkapi.BatchCreator;
import com.sforce.async.*;
import com.sforce.ws.ConnectorConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BulkJobExecutor {
    ConnectorConfig config;
    String sObjectType;
    BatchCreator batchCreator;

    public BulkJobExecutor(ConnectorConfig config, String sObjectType, BatchCreator batchCreator) throws AsyncApiException {
        this.config = config;
        this.sObjectType = sObjectType;
        this.batchCreator = batchCreator;
    }

    public void execute(int totalCount, int batchLimit) throws AsyncApiException, InterruptedException {
        BulkConnection connection = createBulkConnection(config);
        JobInfo job = createJob(sObjectType, connection);
        List<BatchInfo> batchInfos = new ArrayList<>();

        int remainingCount = totalCount;
        int batchCount = 0;
        while (remainingCount > 0) {
            int processedCount = Math.min(remainingCount, batchLimit);
            batchInfos.add(batchCreator.createBatch(connection, job, ++batchCount, processedCount));
            remainingCount -= processedCount;
        }
        connection.closeJob(job.getId());
        awaitCompletion(connection, job.getId(), batchInfos);
    }

    private BulkConnection createBulkConnection(ConnectorConfig config) throws AsyncApiException {
        ConnectorConfig bulkConfig = new ConnectorConfig();
        bulkConfig.setSessionId(config.getSessionId());
        String soapEndpoint = config.getServiceEndpoint();
        String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))
                + "async/" + Constants.API_VERSION;
        bulkConfig.setRestEndpoint(restEndpoint);
        // This should only be false when doing debugging.
        bulkConfig.setCompression(true);
        // Set this to true to see HTTP requests and responses on stdout
        bulkConfig.setTraceMessage(false);

        return new BulkConnection(bulkConfig);
    }

    private static JobInfo createJob(String sObjectType, BulkConnection bulkConnection) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setObject(sObjectType);
        job.setOperation(OperationEnum.insert);
        job.setContentType(ContentType.CSV);
        return bulkConnection.createJob(job);
    }

    private static void awaitCompletion(BulkConnection bulkConnection, String jobId, List<BatchInfo> batchInfoList)
            throws AsyncApiException, InterruptedException {
        Set<String> incomplete = new HashSet<String>();
        for (BatchInfo bi : batchInfoList) {
            incomplete.add(bi.getId());
        }

        long sleepTime = 0L;
        while (!incomplete.isEmpty()) {
            Thread.sleep(sleepTime);
            System.out.println("Awaiting results..." + incomplete.size());
            sleepTime = 10000L;
            BatchInfo[] statusList = bulkConnection.getBatchInfoList(jobId).getBatchInfo();
            for (BatchInfo b : statusList) {
                if (b.getState() == BatchStateEnum.Completed || b.getState() == BatchStateEnum.Failed) {
                    if (incomplete.remove(b.getId())) {
                        System.out.println("BATCH STATUS:\n" + b);
                    }
                }
            }
        }
    }
}
