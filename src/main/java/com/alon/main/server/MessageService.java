package com.alon.main.server;

import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.http.AsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alon.main.server.conf.Const.UTF_8;


/**
 * Created by alon_ss on 6/14/16.
 */
public class MessageService {

    private AsyncHttpClient httpClient;
    private ErrorType errorType;


    public MessageService(ErrorType errorType){
        this.errorType = errorType;
        httpClient = new AsyncHttpClient(errorType);
    }

    public String handleMessage(String jsonBase64, Integer timeout, AggregationType aggregationType) {

        // Decode To Json Array
        JSONArray jsonArray = decodeToJsonArray(jsonBase64);

        // Convert the Json Array to a list of MessageRequest(site and url to execute)
        Set<String> urls = convertToList(jsonArray);

        // Execute the http calls and get a list of the response
        Map<String, JSONObject> urlToObjectMap = doHttpRequest(urls, timeout);

        // Do the error handling logic and create the MessageResponse(site and response)
        Set<JSONObject> jsonObjectSet = urls.stream().map(x -> errorHandling(x, urlToObjectMap)).collect(Collectors.toSet());

        // Do the aggregation logic and return as a String
        return  aggregate(jsonObjectSet, aggregationType);
    }

    private String aggregate(Set<JSONObject> jsonObjectSet, AggregationType aggregationType) {
        switch (aggregationType){
            case APPENDED:
                return appendLogic(jsonObjectSet);
            case COMBINED:
                return combineLogic(jsonObjectSet);
        }

        return null;
    }

    private String combineLogic(Set<JSONObject> jsonObjectSet) {
        JSONObject jsonObjectResponse = new JSONObject();

        for (JSONObject json: jsonObjectSet){
            for (String key: json.keySet()){
                jsonObjectResponse.put(key, json.get(key));
            }
        }

        return jsonObjectResponse.toString();
    }

    private String appendLogic(Set<JSONObject> jsonObjectSet) {
        JSONArray json = new JSONArray();
        for (JSONObject jsonObject: jsonObjectSet){
            json.put(jsonObject);
        }
        return json.toString();
    }

    private JSONObject errorHandling(String url, Map<String, JSONObject> urlToObjectMap) {
        JSONObject result = null;
        switch (errorType){
            case REPLACE:
                result = urlToObjectMap.getOrDefault(url, failJson(url));
                break;
            case FAIL_ANY:
                result = urlToObjectMap.get(url);
                if (result == null){
                    throw new RuntimeException("Problem to execute HTTP call to " + url +
                            "\n  ErrorType == FAIL_ANY. Therefore Fail all the transaction");
                }
                break;
        }
        return result;
    }

    private JSONObject failJson(String key){
        return new JSONObject().put(key, "fail");
    }


    private JSONArray decodeToJsonArray(String jsonBase64) {
        byte[] data1 = Base64.getDecoder().decode(jsonBase64);
        JSONArray json;
        try {
            String josnString = new String(data1, UTF_8);
            json = new JSONArray(josnString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            json = new JSONArray();
        }
        return json;
    }

    private Set<String> convertToList(JSONArray jsonArray) {

        Set<String> set = new HashSet<>(jsonArray.length());
        for (Object object: jsonArray){
            String url = (String) object;
            set.add(url);
        }

        return set;
    }

    private Map<String, JSONObject> doHttpRequest(Set<String> urls, Integer timeout) {

        Map<String, JSONObject> httpResponses;

        try {
            httpResponses = httpClient.doPar(urls, timeout);
        } catch (Exception e) {

            System.err.println("Some problem in http calls");
            e.printStackTrace();
            httpResponses = Collections.EMPTY_MAP;
        }

        return httpResponses;
    }

}
