package com.salesforce.drivers.sfdc;

import com.salesforce.impl.bulkapi.BatchCreator;
import com.salesforce.utils.RandomPhoneNumberGenerator;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;
import com.sforce.soap.enterprise.sobject.Lead;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class InsertBatchCreator implements BatchCreator {

    @Override
    public BatchInfo createBatch(BulkConnection bulkConnection, JobInfo job, int batchSeqId, int recordsCount) throws AsyncApiException {
        Lead[] leads = new Lead[recordsCount];
        for (int i = 0; i < recordsCount; i++) {
            Lead lead = new Lead();
            lead.setLastName("DDV2_LeadLN_" + batchSeqId + "_" + i);
            lead.setCompany("DDV2_LeadCompany_" + batchSeqId + "_" + i);
            if (i % 5 == 0){
                lead.setDescription("Setting fake test data with phone number as " + RandomPhoneNumberGenerator.getRandomNumber());
            }

            if (i % 10 == 0){
                lead.setShort_Description__c("Setting fake test data with fake name as John Doe");
            }
            leads[i] = lead;
        }

        return bulkConnection.createBatchFromStream(job, new ByteArrayInputStream(getCsvData(leads)));
    }

    private static byte[] getCsvData(Lead[] leads) {
        StringBuilder csvData = new StringBuilder();
        csvData.append("LastName").append(",")
                .append("Company").append(",")
                .append("Description").append(",")
                .append("Short_Description__c").append("\n");
        for (Lead lead : leads) {
            csvData.append(lead.getLastName()).append(",")
                    .append(lead.getCompany()).append(",")
                    .append(lead.getDescription()).append(",")
                    .append(lead.getShort_Description__c()).append("\n");
        }
        String data = csvData.toString();
        return data.getBytes(StandardCharsets.UTF_8);
    }
}
