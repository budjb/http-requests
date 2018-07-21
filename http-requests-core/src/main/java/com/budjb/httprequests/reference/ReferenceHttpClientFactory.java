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

package com.budjb.httprequests.reference;

import com.budjb.httprequests.AbstractHttpClientFactory;
import com.budjb.httprequests.HttpClient;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.filter.HttpClientFilterManager;

/**
 * A factory for {@link ReferenceHttpClient}.
 */
public class ReferenceHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Constructor using empty converter and filter managers.
     */
    public ReferenceHttpClientFactory() {
        super();
    }

    /**
     * Constructor using the provided converter and filter managers.
     *
     * @param entityConverterManager  Entity converter manager.
     * @param httpClientFilterManager Filter manager.
     */
    public ReferenceHttpClientFactory(EntityConverterManager entityConverterManager, HttpClientFilterManager httpClientFilterManager) {
        super(entityConverterManager, httpClientFilterManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClient createHttpClient() {
        return new ReferenceHttpClient(getConverterManager(), getFilterManager());
    }
}