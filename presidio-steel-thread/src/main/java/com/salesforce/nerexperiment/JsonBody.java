package com.salesforce.nerexperiment;

public class JsonBody {
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    public String getAnonymizers() {
        return anonymizers;
    }

    public void setAnonymizers(String anonymizers) {
        this.anonymizers = anonymizers;
    }

    private String anonymizers;

    public String getAnalyzer_results() {
        return analyzer_results;
    }

    public void setAnalyzer_results(String analyzer_results) {
        this.analyzer_results = analyzer_results;
    }

    private String analyzer_results;


    public void setLanguage(String language) {
        this.language = language;
    }

    private String language;

    public static JsonBody getJson(String fieldValue){
        JsonBody jsonBody = new JsonBody();
        jsonBody.setText(fieldValue);
        jsonBody.setLanguage("en");
        return jsonBody;
    }

    public static JsonBody getAnonymizerBody(String fieldValue){
        JsonBody jsonBody = new JsonBody();
        jsonBody.setText(fieldValue);
        jsonBody.setAnonymizers("{\n" +
                "           \"DEFAULT\": { \"type\": \"replace\", \"new_value\": \"ANONYMIZED\" },\n" +
                "           \"PHONE_NUMBER\": { \"type\": \"mask\", \"masking_char\": \"*\", \"chars_to_mask\": 4, \"from_end\": true }\n" +
                "       }");
        jsonBody.setAnalyzer_results("{\n" +
                "\t\"start\": 26,\n" +
                "\t\"end\": 38,\n" +
                "\t\"score\": 0.95,\n" +
                "\t\"entity_type\": \"PHONE_NUMBER\"\n" +
                "}");
        return jsonBody;
    }
}
