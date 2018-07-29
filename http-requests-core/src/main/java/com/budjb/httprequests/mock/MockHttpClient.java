/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.budjb.httprequests.mock;

import com.budjb.httprequests.*;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An implementation of {@link HttpClient} that does not make an actual HTTP request; rather it allows
 * the contents of the response to be injected into the client and returned as if a request had been made
 * and those properties were returned in the response. This is useful when mocking requests in unit or
 * integration tests.
 */
public class MockHttpClient extends AbstractHttpClient {
    /**
     * Headers of the response.
     */
    private MultiValuedMap headers = new MultiValuedMap();

    /**
     * Content type of the response.
     */
    private String contentType;

    /**
     * Character set of the response.
     */
    private String charset;

    /**
     * HTTP status code of the response.
     */
    private int status;

    /**
     * Input stream of the response.
     */
    private HttpEntity responseHttpEntity;

    /**
     * A buffer containing the contents of the request input stream.
     */
    private byte[] requestBuffer;

    /**
     * HTTP request context.
     */
    private HttpContext httpContext;

    /**
     * Constructor.
     *
     * @param converterManager Converter manager.
     */
    public MockHttpClient(EntityConverterManager converterManager) {
        super(converterManager);
    }

    /**
     * Converts the entity input stream to an output stream.
     *
     * @param inputStream  Source input stream.
     * @param outputStream Target output stream.
     */
    private static void transmit(InputStream inputStream, OutputStream outputStream) throws IOException {
        int read;
        byte[] buffer = new byte[8192];

        while ((read = inputStream.read(buffer, 0, 8192)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        inputStream.close();
    }

    public MultiValuedMap getHeaders() {
        return headers;
    }

    public void setHeaders(MultiValuedMap headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getRequestBuffer() {
        return requestBuffer;
    }

    public void setRequestBuffer(byte[] requestBuffer) {
        this.requestBuffer = requestBuffer;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public HttpEntity getResponseHttpEntity() {
        return responseHttpEntity;
    }

    public void setResponseHttpEntity(HttpEntity responseHttpEntity) {
        this.responseHttpEntity = responseHttpEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(HttpContext context, HttpEntity httpEntity, HttpClientFilterProcessor filterProcessor) throws IOException {
        httpContext = context;

        if (httpEntity != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            transmit(httpEntity.getInputStream(), filterProcessor.filterOutputStream(outputStream));
            requestBuffer = outputStream.toByteArray();
        }

        return new MockHttpResponse(getConverterManager(), context.getRequest(), status, headers, responseHttpEntity);
    }
}
