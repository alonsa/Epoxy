package com.alon.main.server.service;

import com.alon.main.server.enums.AggregationType;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;


/**
 * Created by alon_ss on 6/15/16.
 */
public class AggregationService {

    public String aggregate(Set<JSONObject> jsonObjectSet, AggregationType aggregationType) {
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
}
