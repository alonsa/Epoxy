package com.alon.main.server.http;

import com.alon.main.server.entity.HttpResponseEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alon.main.server.conf.Conf.CONTENT_TYPE;


/**
 * Created by alon_ss on 6/13/16.
 */

public class HttpAsyncHttpClient {

    public Map<String, HttpResponseEntity> doPar(Set<String> urls, Integer timeout) throws Exception{

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
        Map<String, HttpResponseEntity> responses = null;
        try {
            responses = urls.parallelStream().
                    map(url -> getGetAsyncThread(client, url)). // create callables
                    filter(x -> x != null). // in case of bad urls
                    map(GetAsyncThread::call). // execute requests. Return with Url Request as String and a  Future of HttpResponse
                    filter(x -> x != null). // in case of bad http response
                    map(x -> toHttpResponseEntity(x.getUri(), x.getFutureResponse())).
                    filter(x -> x.getBody() != null). // in case of bad http response
                    collect(Collectors.toMap(HttpResponseEntity::getUrl, Function.identity()));
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

    private HttpResponseEntity toHttpResponseEntity(String url, Future<HttpResponse> httpFutureResponse) {
        HttpResponseEntity response = new HttpResponseEntity();

        if (httpFutureResponse != null){
            try {
                response = toHttpResponseEntity(url, httpFutureResponse.get());
            } catch (Exception ignored) {

            }
        }
        return response;
    }

    private HttpResponseEntity toHttpResponseEntity(String url, HttpResponse httpResponse){
        String body = null;
        String contentType = null;
        if (httpResponse.getStatusLine().getStatusCode() < 300){
            try {
                contentType = httpResponse.getFirstHeader(CONTENT_TYPE).getValue();
                InputStream inputStream = httpResponse.getEntity().getContent();
                body =  IOUtils.toString(inputStream).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HttpResponseEntity(contentType, body, url);
    }

    private static class GetAsyncThread implements Callable<Pair> {
        private CloseableHttpAsyncClient client;
        private HttpContext context;
        private HttpGet request;

        GetAsyncThread(CloseableHttpAsyncClient client, HttpGet req){
            this.client = client;
            context = HttpClientContext.create();
            this.request = req;
        }

        @Override
        public Pair call() {
            Pair response;

            try{
                Future<HttpResponse> futureResponse = client.execute(request, context, null);
                String uri = request.getURI().toString();
                response = new Pair(uri, futureResponse);
            }catch (Exception e) {
                System.err.println("Failed to call to: " + request);
                response = null;
            }

            return response;
        }
    }

    private static class Pair {
        String uri;
        Future<HttpResponse> futureResponse;

        Pair(String uri, Future<HttpResponse> futureResponse) {
            this.uri = uri;
            this.futureResponse = futureResponse;
        }

        String getUri() {
            return uri;
        }

        Future<HttpResponse> getFutureResponse() {
            return futureResponse;
        }
    }

}

