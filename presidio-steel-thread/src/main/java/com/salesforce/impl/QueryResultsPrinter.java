package com.salesforce.impl;

import com.sforce.soap.enterprise.sobject.Lead;
import com.sforce.soap.enterprise.sobject.SObject;

public class QueryResultsPrinter {

    public static void print(SObject[] sObjects){
        for (SObject record : sObjects) {
            Lead lead = (Lead) record;
            String description = lead.getDescription();
            String dob = String.valueOf(lead.getDOB__c());
            String email = lead.getEmail();
            String id = lead.getId();
            String name = lead.getName();
            String phone = lead.getPhone();
            String short_desc = lead.getShort_Description__c();
            System.out.println("Description:" + description + "\t DOB: " + dob + "\t Email: "
                    + email + "\t Id: " + id + "\t Name: " + name + "\t Phone: " + phone +
                    "\t ShortDescription: " + short_desc);
        }
    }
}
