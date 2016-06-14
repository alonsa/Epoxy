package com.alon.main.server.entity;

/**
 * Created by alon_ss on 6/13/16.
 */
public class MessageRequest {
    private String site;
    private String url;

    public MessageRequest() {}

    public MessageRequest(String site, String url) {
        this.site = site;
        this.url = url;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "site='" + site + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
