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

        for (JSONObject json: jsonObjectSet){
            for (String key: json.keySet()){
                jsonObjectResponse.put(key, json.get(key));
            }
        }

        return jsonObjectResponse.toString();
    }

    private static String appendLogic(Set<JSONObject> jsonObjectSet) {
        JSONArray json = new JSONArray();
        for (JSONObject jsonObject: jsonObjectSet){
            json.put(jsonObject);
        }
        return json.toString();
    }
}
