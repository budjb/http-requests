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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates {@link HttpClientFilter} objects and provides a single entry-point to their
 * various callbacks.
 */
public class HttpClientFilterManager {
    /**
     * List of registered filters.
     */
    private final List<HttpClientFilter> filters = new ArrayList<>();

    /**
     * Base constructor.
     */
    public HttpClientFilterManager() {
    }

    /**
     * Creates a filter manager with the contents of another manager.
     *
     * @param other Other filter manager to make a copy of.
     */
    public HttpClientFilterManager(HttpClientFilterManager other) {
        other.getAll().forEach(this::add);
    }

    /**
     * Adds a filter to the manager.
     *
     * @param filter Filter to add to the manager.
     */
    public void add(HttpClientFilter filter) {
        filters.add(filter);
    }

    /**
     * Returns the list of registered filters.
     *
     * @return List of registered filters.
     */
    public List<HttpClientFilter> getAll() {
        return filters;
    }

    /**
     * Remove a filter.
     *
     * @param filter Filter to remove.
     */
    public void remove(HttpClientFilter filter) {
        filters.remove(filter);
    }

    /**
     * Remove all filters.
     */
    public void clear() {
        filters.clear();
    }

    /**
     * Return a list of all registered {@link RequestFilter} instances.
     *
     * @return All registered {@link RequestFilter} instances.
     */
    public List<RequestFilter> getRequestFilters() {
        return filters.stream().filter(f -> f instanceof RequestFilter).map(f -> (RequestFilter) f).collect(Collectors.toList());
    }

    /**
     * Return a list of all registered {@link ResponseFilter} instances.
     *
     * @return A list of all registered {@link ResponseFilter} instances.
     */
    public List<ResponseFilter> getResponseFilters() {
        return filters.stream().filter(f -> f instanceof ResponseFilter).map(f -> (ResponseFilter) f).collect(Collectors.toList());
    }

    /**
     * Return a list of all registered {@link OutputStreamFilter} instances.
     *
     * @return A list of all registered {@link OutputStreamFilter} instances.
     */
    public List<OutputStreamFilter> getRequestEntityFilters() {
        return filters.stream().filter(f -> f instanceof OutputStreamFilter).map(f -> (OutputStreamFilter) f).collect(Collectors.toList());
    }

    /**
     * Return a list of all registered {@link LifecycleFilter} instances.
     *
     * @return A list of all registered {@link LifecycleFilter} instances.
     */
    public List<LifecycleFilter> getLifecycleFilters() {
        return filters.stream().filter(f -> f instanceof LifecycleFilter).map(f -> (LifecycleFilter) f).collect(Collectors.toList());
    }

    /**
     * Return a list of all registered {@link LifecycleFilter} instances.
     *
     * @return A list of all registered {@link LifecycleFilter} instances.
     */
    public List<RetryFilter> getRetryFilters() {
        return filters.stream().filter(f -> f instanceof RetryFilter).map(f -> (RetryFilter) f).collect(Collectors.toList());
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
}
