package com.alon.main.server.utill;

import com.alon.main.server.enums.AggregationType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;


/**
 * Created by alon_ss on 6/15/16.
 */
public class AggregationUtil {

    public static String aggregate(Set<JSONObject> jsonObjectSet, AggregationType aggregationType) {
        switch (aggregationType){
            case APPENDED:
                return appendLogic(jsonObjectSet);
            case COMBINED:
                return combineLogic(jsonObjectSet);
        }

        return null;
    }

    private static String combineLogic(Set<JSONObject> jsonObjectSet) {
        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectSet.forEach(x -> addToJson(jsonObjectResponse, x));

        return jsonObjectResponse.toString();
    }

    private static String appendLogic(Set<JSONObject> jsonObjectSet) {
        JSONArray json = new JSONArray();
        for (JSONObject jsonObject: jsonObjectSet){
            json.put(jsonObject);
        }
        return json.toString();
    }

    /*
     Implementation that add a json keys and values to the "master" Json
     */
    private static void addToJson(JSONObject masterJson, JSONObject jsonToAdd) {
        jsonToAdd.keySet().forEach(key -> masterJson.put(key, jsonToAdd.get(key)));
    }

//    /*
//     Implementation that take a json and strip it to its smallest values
//      and then add it keys and values to the "master" Json
//    */
//    private static void addToJson(JSONObject masterJson, JSONObject jsonToAdd) {
//        jsonToAdd.keySet().forEach(key -> flattObject(masterJson, key, jsonToAdd.get(key)));
//    }
//
//    private static void flatten(JSONObject masterJson, JSONObject jsonToFlatt) {
//        for (String key: jsonToFlatt.keySet()){
//            Object value = jsonToFlatt.get(key);
//            flattObject(masterJson, key, value);
//        }
//    }
//
//    private static void flattObject(JSONObject masterJson, String key, Object value) {
//        if (value instanceof JSONObject){
//            flatten(masterJson, (JSONObject)value);
//        }else if (value instanceof JSONArray){
//            JSONArray jsonArray = ((JSONArray) value);
//            for (Object obj :jsonArray){
//                flattObject(masterJson, key, obj);
//            }
//        }else{
//            masterJson.put(key, value);
//        }
//    }
}
