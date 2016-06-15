package com.alon.main.server.http;

import com.alon.main.server.Parser;
import com.alon.main.server.enums.ErrorType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * Created by alon_ss on 6/13/16.
 */

public class AsyncHttpClient {
    private Parser parser;
    private ErrorType errorType;

    public AsyncHttpClient(ErrorType errorType){
        this.parser = new Parser();
        this.errorType = errorType;
    }

    public Map<String, JSONObject> doPar(Set<String> urls, Integer timeout) throws Exception{

        // Create Http Client
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if (timeout != null){
            requestConfigBuilder.setConnectTimeout(timeout).setSocketTimeout(timeout);
        }
        RequestConfig requestConfig = requestConfigBuilder.build();

        CloseableHttpAsyncClient client = HttpAsyncClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();

        client.start();

        // Async
        Map<String, JSONObject> responses = null;
        try {
            responses = urls.parallelStream().
                    map(url -> getGetAsyncThread(client, url)). // create callables
                    filter(x -> failMechanism(x, "One of the Urls was a bad url")). // in case of bad urls
                    map(GetAsyncCall::call). // execute requests. Return with Url Request as String and a  Future of HttpResponse
                    filter(x -> failMechanism(x.getValue(), "Failed to call to: " + x.getKey())). // in case of bad http response
                    map(x -> toHttpResponseEntity(x.getKey(), x.getValue())).
                    filter(x -> failMechanism(x.getValue(), "Bad response for: " + x.getKey())). // in case of bad http response
                    collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        }finally {
            try{
                client.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return responses;
    }

    private boolean failMechanism(Object x, String errMessage) {
        boolean response = true;
        switch (errorType){
            case REPLACE:
                response =  x != null;
                break;
            case FAIL_ANY:
                if (x == null){
                    throw new RuntimeException(errMessage);
                }
        }
        return response;
    }

    private GetAsyncCall getGetAsyncThread(CloseableHttpAsyncClient client, String url) {
        try {
            HttpGet request = new HttpGet(url);
            return new GetAsyncCall(client, request);
        } catch (Exception e) {
            return null;
        }
    }

    private Pair<String, JSONObject> toHttpResponseEntity(String url, Future<HttpResponse> httpFutureResponse) {
        JSONObject obj = null;

        try {
            HttpResponse httpResponse = httpFutureResponse.get();

            if (httpResponse.getStatusLine().getStatusCode() < 300){
                try {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    ContentType contentType = ContentType.get(httpEntity);
                    obj = parser.parse(inputStream, contentType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(url, obj);
    }

    private static class GetAsyncCall implements Callable<Pair<String, Future<HttpResponse>>> {
        private CloseableHttpAsyncClient client;
        private HttpContext context;
        private HttpGet request;

        GetAsyncCall(CloseableHttpAsyncClient client, HttpGet req){
            this.client = client;
            context = HttpClientContext.create();
            this.request = req;
        }

        @Override
        public Pair<String, Future<HttpResponse>> call() {

            Future<HttpResponse> futureResponse = null;
            String uri = request.getURI().toString();
            try{
                futureResponse = client.execute(request, context, null);
            }catch (Exception e) {
                System.err.println("Failed to call to: " + request);
            }

            return new Pair<>(uri, futureResponse);
        }
    }

    private static class Pair<K, V> {
        K key;
        V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return this.key;
        }

        V getValue() {
            return this.value;
        }
    }

}

