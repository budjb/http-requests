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

/**
 * A filter that allows fairly low-level manipulation of the HTTP context throughout
 * the various stages of the request lifecycle. Concrete implementations of this
 * interface should take care to not adversely interfere with the state of the request.
 */
public interface LifecycleFilter extends HttpClientFilter {
    /**
     * Called before any processing of the request has started. This is the first callback
     * opportunity available during the lifecycle of request processing.
     *
     * @param context HTTP context.
     */
    default void onStart(HttpContext context) {

    }

    /**
     * Called immediately before the request is actually executed, and after all filters
     * have been applied.
     *
     * @param context HTTP context.
     */
    default void onRequest(HttpContext context) {

    }

    /**
     * Called before the response is returned after the request is executed, and after
     * all filters have been applied. Note that the request may still be retried if an
     * implementation returns true for {@link RetryFilter#isRetryRequired}}.
     *
     * @param context HTTP context.
     */
    default void onResponse(HttpContext context) {

    }

    /**
     * Called immediately before the response is returned after the request is executed.
     * This  method will only be called once no more retries are attempted, and is the
     * final callback opportunity before the response is returned to the caller.
     *
     * @param context HTTP context.
     */
    default void onComplete(HttpContext context) {

    }
}