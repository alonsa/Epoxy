package com.alon.main.server.entity;


import com.alon.main.server.enums.ContentTypeEnum;

/**
 * Created by alon_ss on 6/13/16.
 */
public class HttpResponseEntity {

    private String url;
    private ContentTypeEnum contentType;
    private String body;

    public HttpResponseEntity() {
    }

    public HttpResponseEntity(String contentType, String body, String url) {
        this.contentType = ContentTypeEnum.fromString(contentType);
        this.body = body;
        this.url = url;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
