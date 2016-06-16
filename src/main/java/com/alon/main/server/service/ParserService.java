package com.alon.main.server.service;

import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.json.XML;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;


/**
 * Created by alon_ss on 6/15/16.
 */
public class ParserService {

    public JSONObject parse(String str, ContentType contentType){
        switch (contentType.getMimeType()){
            case APPLICATION_JSON: return parseJson(str);
            case TEXT_XML: return parseXml(str);
            default: return parseJson(str);
        }
    }

    private JSONObject parseJson(String str) {
        return new JSONObject(str);
    }

    private JSONObject parseXml(String str) {
        JSONObject xmlJSONObj = null;
        try
        {
            xmlJSONObj = XML.toJSONObject(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlJSONObj;
    }
}
