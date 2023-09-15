package com.salesforce.impl;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class QueryResultsFetcher {
    public static QueryResult query(boolean shouldPrintConsole) {
        QueryResult queryResult = null;
        try {
            EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
            queryResult = connection.query(Constants.QUERY);
            boolean done = false;
            if (queryResult.getSize() > 0) {
                System.out.println("Logged-in user can see a total of "
                        + queryResult.getSize() + " contact records.");
                while (!done) {
                    SObject[] records = queryResult.getRecords();
                    if (shouldPrintConsole){
                        QueryResultsPrinter.print(records);
                    }
                    if (queryResult.isDone()) {
                        done = true;
                    } else {
                        queryResult = connection.queryMore(queryResult.getQueryLocator());
                    }
                }
            } else {
                System.out.println("No records found.");
            }
            System.out.println("\nQuery successfully executed.");
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return queryResult;
    }
}
