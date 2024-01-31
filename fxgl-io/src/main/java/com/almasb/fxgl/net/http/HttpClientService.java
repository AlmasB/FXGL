/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.http;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.concurrent.IOTask;
import com.almasb.fxgl.core.util.LazyValue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

/**
 * Service that allows sending HTTP requests.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class HttpClientService extends EngineService {

    // once built it is immutable, so safe to share
    private LazyValue<HttpClient> client = new LazyValue<>(HttpClient::newHttpClient);

    /**
     * @return task that sends a GET request
     */
    public IOTask<HttpResponse<String>> sendGETRequestTask(String url) {
        return sendGETRequestTask(url, Collections.emptyMap());
    }

    /**
     * @return task that sends a GET request with given [headers]
     */
    public IOTask<HttpResponse<String>> sendGETRequestTask(String url, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder(URI.create(url))
                .GET();

        headers.forEach(builder::header);

        return sendRequestTask(builder.build());
    }

    /**
     * @return task that sends a GET request with given [headers] and given response body [handler]
     */
    public <T> IOTask<HttpResponse<T>> sendGETRequestTask(String url, Map<String, String> headers, HttpResponse.BodyHandler<T> handler) {
        var builder = HttpRequest.newBuilder(URI.create(url))
                .GET();

        headers.forEach(builder::header);

        return sendRequestTask(builder.build(), handler);
    }

    /**
     * @return task that sends a PUT request with given [body]
     */
    public IOTask<HttpResponse<String>> sendPUTRequestTask(String url, String body) {
        return sendPUTRequestTask(url, body, Collections.emptyMap());
    }

    /**
     * @return task that sends a PUT request with given [body] and [headers]
     */
    public IOTask<HttpResponse<String>> sendPUTRequestTask(String url, String body, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(body));

        headers.forEach(builder::header);

        return sendRequestTask(builder.build());
    }

    /**
     * @return task that sends a POST request with given [body]
     */
    public IOTask<HttpResponse<String>> sendPOSTRequestTask(String url, String body) {
        return sendPOSTRequestTask(url, body, Collections.emptyMap());
    }

    /**
     * @return task that sends a POST request with given [body] and [headers]
     */
    public IOTask<HttpResponse<String>> sendPOSTRequestTask(String url, String body, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body));

        headers.forEach(builder::header);

        return sendRequestTask(builder.build());
    }

    /**
     * @return task that sends a DELETE request
     */
    public IOTask<HttpResponse<String>> sendDELETERequestTask(String url) {
        return sendDELETERequestTask(url, Collections.emptyMap());
    }

    /**
     * @return task that sends a DELETE request with given [headers]
     */
    public IOTask<HttpResponse<String>> sendDELETERequestTask(String url, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder(URI.create(url))
                .DELETE();

        headers.forEach(builder::header);

        return sendRequestTask(builder.build());
    }

    /**
     * Constructs a HTTP IO task.
     * The response body will be received as a String.
     *
     * @return task that sends given [request]
     */
    public IOTask<HttpResponse<String>> sendRequestTask(HttpRequest request) {
        return sendRequestTask(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Constructs a HTTP IO task.
     * The response body will be received as a type T.
     *
     * @return task that sends given [request]
     */
    public <T> IOTask<HttpResponse<T>> sendRequestTask(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
        return IOTask.of("sendRequestTask",
                () -> client.get().send(request, handler)
        );
    }
}
