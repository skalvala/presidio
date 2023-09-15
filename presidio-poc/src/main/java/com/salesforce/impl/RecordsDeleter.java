package com.salesforce.impl;

import com.sforce.soap.enterprise.DeleteResult;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Error;
import com.sforce.ws.ConnectionException;

public class RecordsDeleter {

    public static void deleteRecords(String[] ids) throws Exception {
        try {
            EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
            DeleteResult[] deleteResults = connection.delete(ids);
            for (int i = 0; i < deleteResults.length; i++) {
                DeleteResult deleteResult = deleteResults[i];
                if (deleteResult.isSuccess()) {
                    System.out
                            .println("Deleted Record ID: " + deleteResult.getId());
                } else {
                    // Handle the errors.
                    // We just print the first error out for sample purposes.
                    Error[] errors = deleteResult.getErrors();
                    if (errors.length > 0) {
                        System.out.println("Error: could not delete " + "Record ID "
                                + deleteResult.getId() + ".");
                        System.out.println("   The error reported was: ("
                                + errors[0].getStatusCode() + ") "
                                + errors[0].getMessage() + "\n");
                    }
                }
            }
        } catch (ConnectionException ce) {
            ce.printStackTrace();
        }
    }

}
