package com.salesforce.drivers.sfdc;

import com.salesforce.impl.QueryResultsFetcher;
import com.sforce.ws.ConnectionException;

public class RecordsQueryDriver {

    public static void main(String[] args) throws ConnectionException {
        QueryResultsFetcher.query(true);
    }
}
