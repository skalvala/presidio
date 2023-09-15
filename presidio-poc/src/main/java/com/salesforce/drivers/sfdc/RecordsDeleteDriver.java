package com.salesforce.drivers.sfdc;

import com.salesforce.impl.*;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.SObject;

import java.util.ArrayList;
import java.util.List;

public class RecordsDeleteDriver {

    public static void main(String[] args) throws Exception {
        List<String> ids = new ArrayList<>();
        EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
        QueryResult queryResult = connection.query(Constants.QUERY);
        boolean done = false;
        if (queryResult.getSize() > 0) {
            System.out.println("Logged-in user can see a total of "
                    + queryResult.getSize() + " contact records.");
            while (!done) {
                SObject[] records = queryResult.getRecords();
                for (SObject record: records){
                    ids.add(record.getId());
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

        com.salesforce.impl.RecordsDeleter.deleteRecords(ids.toArray(new String[ids.size()]));
    }

}
