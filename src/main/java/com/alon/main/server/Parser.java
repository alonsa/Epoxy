package com.alon.main.server;

import com.alon.main.server.entity.HttpResponseEntity;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

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
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( new InputSource( new StringReader( xmlString ) ) );

//            document = builder.parse(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
}
