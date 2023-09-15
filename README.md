## Importing Maven project
1. Download contents of this git repo (or do git clone) on your local folder, say ~/Documents/Presidio
2. Import the presidio-poc folder as the parent Maven project into intellij and not this top level git repo folder (if that makes sense).
(YES - I could have set this in a better way) 

## Procedure to create standalone java program that performs DML & query ops using SFDC API

Reference links used:
https://dev.to/salesforcedevs/build-a-java-backend-that-connects-with-salesforce-3km2
https://resources.docs.salesforce.com/latest/latest/en-us/sfdc/pdf/salesforce_developer_environment_tipsheet.pdf

1. Created a New Maven Project in Intellij. Do not chose any archetype as part of creation process
2. Download the latest Web Services Connector(WSC) from this site: https://mvnrepository.com/artifact/com.force.api/force-wsc. There will be a "View All" link on a row pertaining to "Files" in this link. Click that and it will lead to a page as follows: https://repo1.maven.org/maven2/com/force/api/force-wsc/59.0.0/
3. Download the following four files to your local machine in a folder location (say ~/Documents/Presidio)
    ```
    force-wsc-59.0.0-javadoc.jar
    force-wsc-59.0.0-sources.jar
    force-wsc-59.0.0-uber.jar
    force-wsc-59.0.0.jar
    ```
4. Log in to the actual production salesforce org. Go to Setup -> API and click on "Generate Enterprise WSDL". This will open a new tab in your browser, displaying your WSDL file. Save this file to your local machine with the name sfdc.wsdl. Put it in the same folder where you downloaded the WSC .jar files.
5. To use the SOAP API with Java, we need to generate .jar stub files for use in our application project. From the folder location where the jar files were saved, run the following command to generate the stub file:
   ```
   java –classpath force-wsc-59.0.0-uber.jar com.sforce.ws.tools.wsdlc sfdc.wsdl sfdc_stub.jar
   ```

   This follows the convention as
   ```
   java –classpath <path_to_WSC_jar_filename> com.sforce.ws.tools.wsdlc <path_to_downloaded_WSDL_filename> <path_to output_stub_jar_filename>
   ```

   FWIW executing the following command
   ```
   /usr/libexec/java_home -V
   ```

   gives output as <br />
   ```
   Matching Java Virtual Machines (1):
   sfdc-11.0.19-zulu11.64.20-sa (x86_64) "Azul Systems, Inc." - "Zulu 11" /Library/Java/JavaVirtualMachines/sfdc-openjdk_11.0.19_11.64.20.jdk/Contents/Home/Library/Java/JavaVirtualMachines/sfdc-openjdk_11.0.19_11.64.20.jdk/Contents/Home
   ```
   Doing echo $JAVA_HOME does not output anything. The above step creates sfdc_stub.jar.
6. In Intellij, go to Files -> Project Structure -> Project Settings -> Libraries and add the 5 jar files onto the classpath.
![Screenshot 2023-09-15 at 7.38.09 AM.png](..%2F..%2F..%2FDesktop%2FScreenshot%202023-09-15%20at%207.38.09%20AM.png)
7. You may run a sample java program that queries against a production org (say GS0) as follows in pertinent package structure:
   ```
   package com.salesforce.drivers.sfdc;

   import com.salesforce.impl.QueryResultsFetcher;
   import com.sforce.ws.ConnectionException;

   public class RecordsQueryDriver {

   private static final String QUERY = "SELECT Description, Email, Id, Name, Phone FROM Lead WHERE CreatedDate >= YESTERDAY";

    public static void main(String[] args) throws ConnectionException {
        QueryResult queryResult = null;
        try {
            EnterpriseConnection connection = SfdcConnector.getEnterpriseConnection();
            queryResult = connection.query(QUERY);
            boolean done = false;
            if (queryResult.getSize() > 0) {
                System.out.println("Logged-in user can see a total of "
                        + queryResult.getSize() + " lead records.");
                while (!done) {
                    SObject[] records = queryResult.getRecords();
                    System.out.println("Description:" + description + "\t Email: "
                    + email + "\t Id: " + id + "\t Name: " + name + "\t Phone: " + phone);
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

   ```

## Procedure to setup and interact with Presidio

Presidio being referred in this:
https://microsoft.github.io/presidio/

1. Install Presidio by cloning the repo in the ~/Documents/Presidio folder by following instructions here
   https://microsoft.github.io/presidio/installation/#install-from-source
   I may have followed more specific steps from here
   https://github.com/microsoft/presidio/blob/main/docs/development.md
   If you have issues with python/pip, try python3/pip3
2. Once presidio is up and running, you can download the postman collection for analyzing and anonymizing from following links
   https://microsoft.github.io/presidio/samples/docker/PresidioAnalyzer.postman_collection.json
   https://microsoft.github.io/presidio/samples/docker/PresidioAnonymizer.postman_collection.json
3. The REST API usage references for both analyzing & anonymizing are here
   https://microsoft.github.io/presidio/api-docs/api-docs.html

## Landing area for this project
Go through this demo and it will give an idea of landing area:
https://drive.google.com/file/d/1GVNBrUmOktyaNSw82RTGVuLUbJ6R4uq9/view?usp=drive_link
