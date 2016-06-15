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

    AsyncHttpClient httpClient;
    private Parser parser;


    public MessageService(){
        httpClient = new AsyncHttpClient();
        parser = new Parser();
    }

    public String handleMessage(String jsonBase64, Integer timeout, ErrorType errorType, AggregationType aggregationType) {

        // Decode To Json Array
        JSONArray jsonArray = decodeToJsonArray(jsonBase64);

        // Convert the Json Array to a list of MessageRequest(site and url to execute)
        Set<String> urls = convertToList(jsonArray);

        // Execute the http calls and get a list of the response
        Map<String, Object> urlToObjectMap = doHttpRequest(urls, timeout);

        // Do the error handling logic and create the MessageResponse(site and response)
        Map<String, Object> urlToResponseMap = urls.stream().
                collect(Collectors.toMap(x-> x, x -> errorHandling(x, urlToObjectMap, errorType)));

        // Do the aggregation logic and return as a String
        return  aggregate(urlToResponseMap, aggregationType);
    }

    private String aggregate(Map<String, Object> urlToResponseMap, AggregationType aggregationType) {
        switch (aggregationType){
            case APPENDED:
                return appendLogic(urlToResponseMap);
            case COMBINED:
                return combineLogic(urlToResponseMap);
        }

        return null;
    }

    private String appendLogic(Map<String, Object> urlToResponseMap) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, Object> entry: urlToResponseMap.entrySet()){
            json.put(entry.getKey(), entry.getValue());
        }

        return json.toString();
    }

    private String combineLogic(Map<String, Object> urlToResponseMap) {
        JSONArray json = new JSONArray();

        List<JSONObject> jsonList = urlToResponseMap.entrySet().stream().map(x -> (new JSONObject()).put(x.getKey(), x.getValue())).collect(Collectors.toList());
        json.put(jsonList);

        return json.toString();
    }

    private Object errorHandling(String url, Map<String, Object> urlToObjectMap, ErrorType errorType) {
        Object result = null;
        switch (errorType){
            case REPLACE:
                result = urlToObjectMap.getOrDefault(url, "fail");
                break;
            case FAIL_ANY:
                result = urlToObjectMap.get(url);
                if (result == null){
                    throw new RuntimeException("Problem to execute HTTP call to " + url);
                }
                break;
        }
        return result;
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

    private Map<String, Object> doHttpRequest(Set<String> urls, Integer timeout) {

        Map<String, Object> httpResponses;

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
