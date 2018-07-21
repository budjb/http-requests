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
package com.budjb.httprequests;

import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterManager;

public abstract class AbstractHttpClientFactory implements HttpClientFactory {
    /**
     * Instance of {@link EntityConverterManager} which will be used by the {@link HttpClient} and {@link HttpResponse}..
     */
    private final EntityConverterManager converterManager;

    /**
     * Filter manager.
     */
    private final HttpClientFilterManager filterManager;

    /**
     * Constructor.
     */
    protected AbstractHttpClientFactory() {
        this(new EntityConverterManager(), new HttpClientFilterManager());
    }

    /**
     * Constructor.
     *
     * @param entityConverterManager  Entity converter manager.
     * @param httpClientFilterManager Filter manager.
     */
    protected AbstractHttpClientFactory(EntityConverterManager entityConverterManager, HttpClientFilterManager httpClientFilterManager) {
        this.converterManager = entityConverterManager;
        this.filterManager = httpClientFilterManager;
    }

    /**
     * Returns the entity converter manager.
     *
     * @return The entity converter manager.
     */
    @Override
    public EntityConverterManager getConverterManager() {
        return converterManager;
    }

    /**
     * Returns the filter manager.
     *
     * @return The filter manager.
     */
    @Override
    public HttpClientFilterManager getFilterManager() {
        return filterManager;
    }
}
