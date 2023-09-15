package com.salesforce.nerexperiment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

/**
 * Ref: https://www.youtube.com/watch?v=9oq7Y8n1t00
 */
public class PresidioRestApiCaller {

    public static void main(String[] args) throws Exception {

        Transcript transcript = new Transcript();
        transcript.setText("John Smith drivers license is AC432223");
        transcript.setLanguage("en");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        System.out.println("jsonRequest: " + jsonRequest);
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:5002/analyze"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest)).build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse);
        System.out.println(httpResponse.body());
    }
}
