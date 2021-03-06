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
package com.budjb.httprequests.filter;

import com.budjb.httprequests.HttpContext;
import com.budjb.httprequests.HttpRequest;
import com.budjb.httprequests.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates {@link HttpClientFilter} objects and provides a single entry-point to their
 * various callbacks.
 */
public class HttpClientFilterProcessor {
    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(HttpClientFilterProcessor.class);

    /**
     * List of registered filters.
     */
    private final List<HttpClientFilter> filters;

    /**
     * Base constructor.
     *
     * @param filters Filters to register with the filter processor.
     */
    public HttpClientFilterProcessor(List<HttpClientFilter> filters) {
        this.filters = filters;
    }

    /**
     * Returns a list of all registered {@link RequestFilter} instances.
     *
     * @return All registered {@link RequestFilter} instances.
     */
    private List<RequestFilter> getRequestFilters() {
        return filters.stream().filter(f -> f instanceof RequestFilter).map(f -> (RequestFilter) f).collect(Collectors.toList());
    }

    /**
     * Returns a list of all registered {@link ResponseFilter} instances.
     *
     * @return A list of all registered {@link ResponseFilter} instances.
     */
    private List<ResponseFilter> getResponseFilters() {
        return filters.stream().filter(f -> f instanceof ResponseFilter).map(f -> (ResponseFilter) f).collect(Collectors.toList());
    }

    /**
     * Returns a list of all registered {@link OutputStreamFilter} instances.
     *
     * @return A list of all registered {@link OutputStreamFilter} instances.
     */
    private List<OutputStreamFilter> getRequestEntityFilters() {
        return filters.stream().filter(f -> f instanceof OutputStreamFilter).map(f -> (OutputStreamFilter) f).collect(Collectors.toList());
    }

    /**
     * Returns a list of all registered {@link LifecycleFilter} instances.
     *
     * @return A list of all registered {@link LifecycleFilter} instances.
     */
    private List<LifecycleFilter> getLifecycleFilters() {
        return filters.stream().filter(f -> f instanceof LifecycleFilter).map(f -> (LifecycleFilter) f).collect(Collectors.toList());
    }

    /**
     * Returns a list of all registered {@link LifecycleFilter} instances.
     *
     * @return A list of all registered {@link LifecycleFilter} instances.
     */
    private List<RetryFilter> getRetryFilters() {
        return filters.stream().filter(f -> f instanceof RetryFilter).map(f -> (RetryFilter) f).collect(Collectors.toList());
    }

    /**
     * Returns a list of all registered filters that are instances of {@link java.io.Closeable}.
     *
     * @return A list of all registered filters that are instances of {@link java.io.Closeable}.
     */
    private List<Closeable> getCloseableFilters() {
        return filters.stream().filter(f -> f instanceof Closeable).map(f -> (Closeable) f).collect(Collectors.toList());
    }

    /**
     * Calls {@link LifecycleFilter#onStart} for all registered filters.
     *
     * @param context HTTP context.
     */
    public void onStart(HttpContext context) {
        getLifecycleFilters().forEach(f -> f.onStart(context));
    }

    /**
     * Calls {@link LifecycleFilter#onRequest} for all registered filters.
     *
     * @param context HTTP context.
     */
    public void onRequest(HttpContext context) {
        getLifecycleFilters().forEach(f -> f.onRequest(context));
    }

    /**
     * Calls {@link LifecycleFilter#onResponse} for all registered filters.
     *
     * @param context HTTP context.
     */
    public void onResponse(HttpContext context) {
        getLifecycleFilters().forEach(f -> f.onResponse(context));
    }

    /**
     * Calls the {@link LifecycleFilter#onComplete} method for all registered filters.
     *
     * @param context HTTP request context.
     */
    public void onComplete(HttpContext context) {
        getLifecycleFilters().forEach(f -> f.onComplete(context));
    }

    /**
     * Calls {@link RequestFilter#filter} for all registered filters.
     *
     * @param request HTTP request.
     */
    public void filterHttpRequest(HttpRequest request) {
        getRequestFilters().forEach(f -> f.filter(request));
    }

    /**
     * Calls {@link ResponseFilter#filter} for all registered filters.
     *
     * @param response HTTP response.
     */
    public void filterHttpResponse(HttpResponse response) {
        getResponseFilters().forEach(f -> f.filter(response));
    }

    /**
     * Filters the {@link OutputStream} of the request.
     *
     * @param outputStream {@link OutputStream} of the request.
     * @return Filtered {@link OutputStream}.
     */
    public OutputStream filterOutputStream(OutputStream outputStream) {
        for (OutputStreamFilter filter : getRequestEntityFilters()) {
            outputStream = filter.filter(outputStream);
        }
        return outputStream;
    }

    /**
     * Returns whether there is any one registered {@link RetryFilter}.
     *
     * @return Whether there is any one registered {@link RetryFilter}.
     */
    public boolean hasRetryFilters() {
        return filters.stream().anyMatch(f -> f instanceof RetryFilter);
    }

    /**
     * Initiates the retry filters and returns whether at least one of them requested a retry.
     *
     * @param context HTTP request context.
     * @return Whether at least one filter requested a retry.
     */
    public boolean isRetryRequired(HttpContext context) {
        boolean retry = false;

        for (RetryFilter filter : getRetryFilters()) {
            if (filter.isRetryRequired(context)) {
                retry = true;
            }
        }

        return retry;
    }

    /**
     * Closes any filters that implement {@link Closeable}. Every filter is guaranteed to be closed.
     */
    public void close() {
        for (Closeable closeable : getCloseableFilters()) {
            try {
                closeable.close();
            }
            catch (Exception e) {
                log.error("Unhandled exception caught while closing filter " + getClass().getName(), e);
            }
        }
    }
}
