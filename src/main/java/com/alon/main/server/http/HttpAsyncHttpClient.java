package com.alon.main.server.http;

import com.alon.main.server.Parser;
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

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * Created by alon_ss on 6/13/16.
 */

public class HttpAsyncHttpClient {
    private Parser parser;

    public HttpAsyncHttpClient(){
       parser = new Parser();
    }

    public Map<String, Object> doPar(Set<String> urls, Integer timeout) throws Exception{

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
        Map<String, Object> responses = null;
        try {

            responses = urls.parallelStream().
                    map(url -> getGetAsyncThread(client, url)). // create callables
                    filter(x -> x != null). // in case of bad urls
                    map(GetAsyncThread::call). // execute requests. Return with Url Request as String and a  Future of HttpResponse
                    filter(x -> x != null). // in case of bad http response
                    map(x -> toHttpResponseEntity(x.getKey(), x.getValue())).
                    filter(x -> x.getValue() != null). // in case of bad http response
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

    private GetAsyncThread getGetAsyncThread(CloseableHttpAsyncClient client, String url) {
        try {
            HttpGet request = new HttpGet(url);
            return new GetAsyncThread(client, request);
        } catch (Exception e) {
            return null;
        }
    }

    private Pair<String, Object> toHttpResponseEntity(String url, Future<HttpResponse> httpFutureResponse) {
        Pair<String, Object> response = null;

        if (httpFutureResponse != null){
            try {
                response = toHttpResponseEntity(url, httpFutureResponse.get());
            } catch (Exception ignored) {

            }
        }
        return response;
    }

    private Pair<String, Object> toHttpResponseEntity(String url, HttpResponse httpResponse){
        Object obj = null;
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
        return new Pair<>(url, obj);
    }


    private static class GetAsyncThread implements Callable<Pair<String, Future<HttpResponse>>> {
        private CloseableHttpAsyncClient client;
        private HttpContext context;
        private HttpGet request;

        GetAsyncThread(CloseableHttpAsyncClient client, HttpGet req){
            this.client = client;
            context = HttpClientContext.create();
            this.request = req;
        }

        @Override
        public Pair<String, Future<HttpResponse>> call() {
            Pair<String, Future<HttpResponse>> response;

            try{
                Future<HttpResponse> futureResponse = client.execute(request, context, null);
                String uri = request.getURI().toString();
                response = new Pair<>(uri, futureResponse);
            }catch (Exception e) {
                System.err.println("Failed to call to: " + request);
                response = null;
            }

            return response;
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
            return key;
        }

        V getValue() {
            return value;
        }
    }

}

