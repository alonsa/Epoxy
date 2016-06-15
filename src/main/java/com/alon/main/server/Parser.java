package com.alon.main.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;


/**
 * Created by alon_ss on 6/15/16.
 */
public class Parser {

    public JSONObject parse(InputStream inputStream, ContentType contentType){
        switch (contentType.getMimeType()){
            case APPLICATION_JSON: return parseJson(inputStream);
            case TEXT_XML: return parseXml(inputStream);
            default: return parseJson(inputStream);
        }
    }

    private String parseString(InputStream inputStream) {
        String str = null;
        try {
            str = IOUtils.toString(inputStream).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private JSONObject parseJson(InputStream inputStream) {
        String jsonString = parseString(inputStream);
        JSONObject json = new JSONObject(jsonString);
        return json;
    }

    private JSONObject parseXml(InputStream inputStream) {
        JSONObject xmlJSONObj = null;
        try
        {
            String xmlString = parseString(inputStream);
            xmlJSONObj = XML.toJSONObject(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlJSONObj;
    }
}
