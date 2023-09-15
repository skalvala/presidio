package com.salesforce.impl;

public class Constants {
    public static final String QUERY = "SELECT Description, DOB__c, Email, Id, Name, Phone, Short_Description__c FROM Lead WHERE CreatedDate >= YESTERDAY";

    private Constants(){}
}
