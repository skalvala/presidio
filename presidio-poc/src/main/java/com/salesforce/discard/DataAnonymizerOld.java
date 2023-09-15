package com.salesforce.discard;

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
import java.util.Scanner;

public class DataAnonymizerOld {

    public static void main(String[] args) throws ConnectionException, URISyntaxException, IOException, InterruptedException {
        //1. Fetch records
        QueryResult queryResult = QueryResultsFetcher.query(false);
        Lead lead;
        List<String> phoneLeadIds = new ArrayList<>();
        List<String> nameLeadIds = new ArrayList<>();
        if (queryResult != null && queryResult.getSize() > 0) {
            SObject[] records = queryResult.getRecords();
            for (SObject record : records) {
                lead = (Lead) record;
                for (int j = 0; j < 7; j++) {
                    if (getFieldValue(j, lead) != null){
                        // 2. Construct JSON
                        Gson gson = new Gson();
                        String jsonRequest = gson.toJson(JsonBody.getJson(getFieldValue(j, lead)));
                        //System.out.println("json: " + jsonRequest);

                        // 3. Pass to NER
                        HttpRequest analyzerPostRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:5002/analyze"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();

                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpResponse<String> httpResponse = httpClient.send(analyzerPostRequest, HttpResponse.BodyHandlers.ofString());
                        if(!httpResponse.body().equals("[]")){
                            System.out.println("json: " + jsonRequest);
                            //System.out.println(httpResponse);
                            System.out.println("PII Detected!");
                            if (j == 0){
                                phoneLeadIds.add(lead.getId());
                            }
                            if (j == 6){
                                nameLeadIds.add(lead.getId());
                            }
                            // get updated json request
//                            jsonRequest = new Gson().toJson(JsonBody.getAnonymizerBody(getFieldValue(j, lead)));
//                            HttpRequest anonymizerPostRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:5001/anonymize"))
//                                    .header("Content-Type", "application/json")
//                                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();
//                            httpResponse = httpClient.send(anonymizerPostRequest, HttpResponse.BodyHandlers.ofString());
//                            System.out.println(httpResponse);



                        }

                    }
                }
            }
            anonymize(phoneLeadIds.toArray(new String[phoneLeadIds.size()]), true, false);
            anonymize(nameLeadIds.toArray(new String[nameLeadIds.size()]), false, true);
        }
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

    private static void anonymize(String[] leadIdsToAnonymize, boolean askUserForPrompt, boolean isShortDescription) throws ConnectionException {

        boolean userResponse = false;
        if (askUserForPrompt){
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Do you want to anonymize PII?");
            String userOption = myObj.nextLine();  // Read user input
            userResponse = userOption.equals("y");
        }
        
        if (!askUserForPrompt || userResponse){
            updatePiiContainingLeads(leadIdsToAnonymize, isShortDescription);
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
                updatedLead.setShort_Description__c("Setting fake test data with fake name as YYYYYYYYY");
            }
            updatedLeads[i] = updatedLead;
        }
        EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
        connection.update(updatedLeads);
    }

}
