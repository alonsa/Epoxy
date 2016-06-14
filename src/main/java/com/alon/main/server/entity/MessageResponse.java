package com.alon.main.server.entity;

/**
 * Created by alon_ss on 6/13/16.
 */
public class MessageResponse {
    private String site;
    private Object response;

    public MessageResponse() {}

    public MessageResponse(String site, Object response) {
        this.site = site;
        this.response = response;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "site='" + site + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
