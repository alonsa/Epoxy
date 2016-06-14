package com.alon.main.server.service;

import com.alon.main.server.entity.HttpResponseEntity;
import com.alon.main.server.entity.MessageRequest;
import com.alon.main.server.entity.MessageResponse;
import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.http.HttpAsyncHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.alon.main.server.conf.Conf.UTF_8;


/**
 * Created by alon_ss on 6/14/16.
 */
public class MessageService {

    public String handleMessage(String jsonBase64, Integer timeout, ErrorType errorType, AggregationType aggregationType) {

        // Decode To Json Array
        JSONArray jsonArray = decodeToJsonArray(jsonBase64);

        // Convert the Json Array to a list of MessageRequest(site and url to execute)
        List<MessageRequest> messageRequestList = convertToList(jsonArray);

        // Execute the http calls and get a map of url to the response
        Map<String, HttpResponseEntity> urlToHttpResponse = doHttpRequest(messageRequestList, timeout);

        // Map the HTTP responses by the content type to an object (Json or XML)
        Map<String, Object> urlToObjectMap = urlToHttpResponse.entrySet().stream().
                collect(Collectors.toMap(Map.Entry::getKey, entry -> parse(entry.getValue())));


        // Do the error handling logic and create the MessageResponse(site and response)
        Map<String, Object> urlToResponseMap = messageRequestList.stream().
                map(x -> new MessageResponse(x.getSite(), errorHandling(x.getUrl(), urlToObjectMap, errorType))).
                collect(Collectors.toMap(MessageResponse::getSite, MessageResponse::getResponse));

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

//    private JSONArray encodeToString(JSONArray json) {
//        json.toString(2)
//        byte[] data1 = Base64.getEncoder().encodeToString(josn);
//        JSONArray json;
//        try {
//            String josnString = new String(data1, UTF_8);
//            json = new JSONArray(josnString);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            json = new JSONArray();
//        }
//        return json;
//    }

    private List<MessageRequest> convertToList(JSONArray jsonArray) {

        List<MessageRequest> list = new ArrayList<>(jsonArray.length());
        for (Object object: jsonArray){
            JSONObject jsonObject = (JSONObject) object;
            Iterator<String> iter = jsonObject.keys();
            if (iter.hasNext()){
                String site = iter.next();
                MessageRequest messageRequest = new MessageRequest(site, jsonObject.getString(site));
                list.add(messageRequest);
            }
        }

        return list;
    }

    private Map<String, HttpResponseEntity> doHttpRequest(List<MessageRequest> messageRequestList, Integer timeout) {

        Map<String, HttpResponseEntity> map;

        Set<String> toGetUrlList = messageRequestList.stream().map(MessageRequest::getUrl).collect(Collectors.toSet());

        HttpAsyncHttpClient httpClient = new HttpAsyncHttpClient();
        try {
            map = httpClient.doPar(toGetUrlList, timeout);
        } catch (Exception e) {

            System.err.println("Some problem in http calls");
            e.printStackTrace();
            map = Collections.EMPTY_MAP;
        }

        return map;
    }

    private static Object parse(HttpResponseEntity x) {
        if (x.getContentType() != null) {
            switch (x.getContentType()) {
                case TEXT_XML:
                    return parseXml(x.getBody());
                case APPLICATION_JSON:
                    return parseJosn(x.getBody());
            }
        }
        return null;
    }

    private static JSONObject parseJosn(String jsonString) {
        return new JSONObject(jsonString);
    }

    private static Document parseXml(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

}
