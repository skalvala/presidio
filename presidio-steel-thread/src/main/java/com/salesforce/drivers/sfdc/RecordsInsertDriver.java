package com.salesforce.drivers.sfdc;

import com.salesforce.utils.RandomPhoneNumberGenerator;
import com.sforce.soap.enterprise.*;
import com.sforce.soap.enterprise.sobject.Lead;
import com.sforce.ws.*;

public class RecordsInsertDriver {
    public static void main(String[] args) {
        ConnectorConfig credentials = new ConnectorConfig();
        credentials.setUsername("ss@demo.com");
        credentials.setPassword("test1234567");

        EnterpriseConnection connection;
        try {
            int NUM_RECORDS = 50;
            Lead[] leads = new Lead[NUM_RECORDS];
            connection = Connector.newConnection(credentials);
            for (int i = 0; i < NUM_RECORDS; i++){
                Lead lead = new Lead();
                lead.setLastName("DDV2_LeadLN" + i);
                lead.setCompany("DDV2_LeadCompany" + i);
                if (i % 5 == 0){
                    lead.setDescription("Setting fake test data with phone number as " + RandomPhoneNumberGenerator.getRandomNumber());
                }

                if (i % 10 == 0){
                    lead.setShort_Description__c("Setting fake test data with fake name as John Doe");
                }
                leads[i] = lead;
            }

            connection.create(leads);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

}
