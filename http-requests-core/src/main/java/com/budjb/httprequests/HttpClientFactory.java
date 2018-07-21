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

/**
 * Describes a factory class that creates an {@link HttpClient} instance. Individual HTTP client libraries
 * should implement this factory for the creation of HTTP clients.
 * <p>
 * These factories are suitable for instantiation as Spring beans so that the factory can be injected where needed.
 */
public interface HttpClientFactory {
    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    HttpClient createHttpClient();

    /**
     * Returns the entity converter manager.
     *
     * @return The entity converter manager.
     */
    EntityConverterManager getConverterManager();

    /**
     * Returns the filter manager.
     *
     * @return The filter manager.
     */
    HttpClientFilterManager getFilterManager();
}
