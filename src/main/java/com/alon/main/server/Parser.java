package com.alon.main.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_XML;


/**
 * Created by alon_ss on 6/15/16.
 */
public class Parser {

    public Object parse(InputStream inputStream, ContentType contentType){
        switch (contentType.getMimeType()){
            case APPLICATION_JSON: return parseJson(inputStream);
            case TEXT_XML: return parseXml(inputStream);
            default: return parseString(inputStream);
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
        return new JSONObject(inputStream);
    }

    private Document parseXml(InputStream inputStream) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try
        {
            String xmlString = parseString(inputStream);
            InputStream stream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));

            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
}
