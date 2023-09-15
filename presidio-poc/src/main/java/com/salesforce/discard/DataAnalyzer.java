package com.salesforce.discard;

import com.google.gson.Gson;
import com.salesforce.impl.QueryResultsFetcher;
import com.salesforce.nerexperiment.JsonBody;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Lead;
import com.sforce.soap.enterprise.sobject.SObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DataAnalyzer {

    public static void main(String[] args) throws Exception {
        //1. Fetch records
        QueryResult queryResult = QueryResultsFetcher.query(false);
        Lead lead;
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
                        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:5002/analyze"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();

                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpResponse<String> httpResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
                        if(!httpResponse.body().equals("[]")){
                            System.out.println("json: " + jsonRequest);
                            //System.out.println(httpResponse);
                            System.out.println("PII Detected!");
                        }

                    }
                }
            }
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

}
