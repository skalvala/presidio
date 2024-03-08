package com.salesforce.impl.bulkapi;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;

@FunctionalInterface
public interface BatchCreator {
    BatchInfo createBatch(BulkConnection bulkConnection, JobInfo job, int batchSeqId, int recordsCount)
            throws AsyncApiException;
}
