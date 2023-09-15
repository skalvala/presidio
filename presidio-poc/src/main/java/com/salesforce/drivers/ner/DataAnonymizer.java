package com.salesforce.drivers.ner;

import com.google.gson.Gson;
import com.salesforce.impl.QueryResultsFetcher;
import com.salesforce.impl.SfdcConnector;
import com.salesforce.nerexperiment.JsonBody;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Lead;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DataAnonymizer {

    public static void main(String[] args) throws ConnectionException, URISyntaxException, IOException, InterruptedException {
        //1. Fetch records
        QueryResult queryResult = QueryResultsFetcher.query(false);
        Lead lead;
        List<String> phoneLeadIds = new ArrayList<>();
        List<String> nameLeadIds = new ArrayList<>();
        if (queryResult != null && queryResult.getSize() > 0) {
            SObject[] records = queryResult.getRecords();
            //int cntr = 1;
            for (SObject record : records) {
                lead = (Lead) record;
                for (int j = 0; j < 7; j++) {
                    if (getFieldValue(j, lead) != null){
                        // 2. Construct JSON
                        Gson gson = new Gson();
                        String jsonRequest = gson.toJson(JsonBody.getJson(getFieldValue(j, lead)));

                        // 3. Pass to NER
                        HttpRequest analyzerPostRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:5002/analyze"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();

                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpResponse<String> httpResponse = httpClient.send(analyzerPostRequest, HttpResponse.BodyHandlers.ofString());
                        if(!httpResponse.body().equals("[]")){
                            if (j == 0){
                                //System.out.println("PII Detected!");
//                                System.out.println("Record Id " + cntr++ + " + : " + lead.getId() +
//                                        "\t Entity_Type: " + httpResponse.body().split("entity_type")[1].split("recognition_metadata")[0].split(",")[0].split(":")[1] +
//                                        "\t Score: " + httpResponse.body().split("score")[1].split(",")[0].split(":")[1]
//                                        +  "\t json: " + jsonRequest);
                                phoneLeadIds.add(lead.getId());
                            }
                            if (j == 6){
                                //System.out.println("PII Detected!");
//                                System.out.println("Record Id: " + (phoneLeadIds.contains(lead.getId()) ? "------------------" : lead.getId()) +
//                                        "\t Entity_Type: " + httpResponse.body().split("entity_type")[1].split("recognition_metadata")[0].split(",")[0].split(":")[1] +
//                                        "\t Score: " + httpResponse.body().split("score")[1].split(",")[0].split(":")[1]
//                                        +  "\t json: " + jsonRequest);
                                nameLeadIds.add(lead.getId());
                            }
                        }
                    }
                }
            }

            if (phoneLeadIds.isEmpty() && nameLeadIds.isEmpty()){
                System.out.println("No records with PII detected.");
            } else {
                anonymize(phoneLeadIds, nameLeadIds);
                System.out.println("Anonymization completed successfully.");
            }

        }
    }

    private static void anonymize(List<String> phoneLeadIds, List<String> nameLeadIds) throws ConnectionException {
        updatePiiContainingLeads(phoneLeadIds.toArray(new String[phoneLeadIds.size()]), false);
        updatePiiContainingLeads(nameLeadIds.toArray(new String[nameLeadIds.size()]), true);
    }

    static String getFieldValue (int counter, Lead lead){
        switch (counter) {
            case 0:
                return lead.getDescription();
            case 1:
                return lead.getDOB__c() == null ? null : String.valueOf(lead.getDOB__c());
            case 2:
                return lead.getEmail();
            case 3:
                return lead.getId();
            case 4:
                return lead.getName();
            case 5:
                return lead.getPhone();
            case 6:
                return lead.getShort_Description__c();
            default:
                return "";
        }
    }

    private static void updatePiiContainingLeads(String[] leadIdsToAnonymize, boolean isShortDescription) throws ConnectionException {
        Lead[] updatedLeads = new Lead[leadIdsToAnonymize.length];
        for (int i = 0; i < leadIdsToAnonymize.length; i++){
            Lead updatedLead = new Lead();
            updatedLead.setId(leadIdsToAnonymize[i]);
            if (!isShortDescription){
                updatedLead.setDescription("Setting fake test data with phone number as XXX XXX XXXX");
            } else {
                updatedLead.setShort_Description__c("Setting fake test data with fake name as <BLANK>");
            }
            updatedLeads[i] = updatedLead;
        }
        EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
        connection.update(updatedLeads);
    }

}
