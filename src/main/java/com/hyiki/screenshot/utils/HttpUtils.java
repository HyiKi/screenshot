package com.hyiki.screenshot.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * http client utils
 * 注意：
 * 外部用完 ResponseBody body = Response.body() ，body 需要手动关闭
 */
@Slf4j
public class HttpUtils {

    public static OkHttpClient okHttpClientFinal = new OkHttpClient();

    public final static ConnectionPool defaultConnectionPool = new ConnectionPool(20, 5, TimeUnit.MINUTES);

    public static final int DEFAULT_TIME_LENGTH = 1000;

    public static OkHttpClient okHttpClient = okHttpClientFinal.newBuilder()
            .connectionPool(defaultConnectionPool)
            .connectTimeout(40 * DEFAULT_TIME_LENGTH, TimeUnit.MILLISECONDS)
            .writeTimeout(60 * DEFAULT_TIME_LENGTH, TimeUnit.MILLISECONDS)
            .readTimeout(60 * DEFAULT_TIME_LENGTH, TimeUnit.MILLISECONDS)
            .build();

    public static final MediaType FORM_URL_ENCODER = MediaType.parse("application/x-www-form-urlencoded");

    /**
     * 设置请求头
     *
     * @param headersParams
     * @return
     */
    public static Headers setHeaders(Map<String, String> headersParams) {
        Headers.Builder headersBuilder = new okhttp3.Headers.Builder();
        if (headersParams != null) {
            Iterator<String> iterator = headersParams.keySet().iterator();
            while (iterator.hasNext()) {
                final String next = iterator.next();
                headersBuilder.add(next, headersParams.get(next));
            }
        }
        return headersBuilder.build();
    }

    /**
     * 解析响应体
     *
     * @param responseBody
     * @return
     */
    public static String doParseResponseBody(ResponseBody responseBody) {
        try {
            if (Objects.nonNull(responseBody)) {
                // string() 会自动关闭资源
                String response = responseBody.string();
                if (log.isDebugEnabled()) {
                    log.debug("response body:{}", response);
                }
                return response.trim();
            }
        } catch (Throwable th) {
            log.error("http请求调用失败 异常", th);
        }
        return null;
    }

    /**
     * POST 请求
     *
     * @param url           请求url
     * @param data          数据
     * @param headersParams 请求头参数
     * @return
     */
    public static String postFormUrlEncoder(String url, String data, Map<String, String> headersParams) {
        return doParseResponseBody(postResponseBodyFormUrlEncoder(url, data, headersParams, null));
    }

    /**
     * POST 请求
     *
     * @param url           请求url
     * @param data          数据
     * @param headersParams 请求头参数
     * @param requestParams 请求参数
     * @return
     */
    private static ResponseBody postResponseBodyFormUrlEncoder(String url, String data, Map<String, String> headersParams, Map<String, String> requestParams) {
        return postResponseBody(url, data, headersParams, requestParams, FORM_URL_ENCODER);
    }

    /**
     * POST 请求
     *
     * @param url           请求url
     * @param data          数据
     * @param requestParams 请求数据
     * @param headersParams 请求头参数
     * @param mediaType
     * @return
     */
    private static ResponseBody postResponseBody(String url, String data, Map<String, String> headersParams, Map<String, String> requestParams, MediaType mediaType) {
        return postResponseBody(okHttpClient, url, data, headersParams, requestParams, mediaType);
    }

    public static ResponseBody postResponseBody(OkHttpClient client, String url, String data, Map<String, String> headersParams, Map<String, String> requestParams, MediaType mediaType) {
        HttpUrl.Builder httpUrlBuild = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Optional.ofNullable(requestParams).orElse(Collections.emptyMap()).forEach(httpUrlBuild::addQueryParameter);
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headersParams)) {
            builder.headers(setHeaders(headersParams));
        }
        builder.post(RequestBody.create(mediaType, data));
        builder.url(httpUrlBuild.build());
        return execute(client, builder);
    }

    public static ResponseBody execute(OkHttpClient client, Request.Builder requestBuilder) {
        Request request = requestBuilder.build();
        return execute(client, request);
    }

    /**
     * @param client
     * @param request
     * @return
     */
    public static ResponseBody execute(OkHttpClient client, Request request) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("request:{}, header:{}", request, request.headers());
            }
            Response response = client.newCall(request).execute();
            if (log.isDebugEnabled()) {
                log.debug("response:{}", response);
            }
            return response.body();
        } catch (SocketTimeoutException e) {
            log.warn("socket time out exception");
            return null;
        } catch (IOException e) {
            log.error("http throw IOException", e);
            return null;
        }
    }

}
