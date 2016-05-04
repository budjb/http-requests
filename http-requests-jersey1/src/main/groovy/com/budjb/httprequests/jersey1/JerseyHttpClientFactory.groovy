/*
 * Copyright 2016 Bud Byrd
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
package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory
import com.budjb.httprequests.AbstractHttpClientFactory

/**
 * An {@link HttpClientFactory} implementation that creates Jersey 1.x HTTP clients.
 */
class JerseyHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Base constructor that automatically registers the default set of entity converters.
     */
    JerseyHttpClientFactory() {
        super()
    }

    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param registerDefaultConverters Whether to register the default set of entity converters.
     */
    JerseyHttpClientFactory(boolean registerDefaultConverters) {
        super(registerDefaultConverters)
    }

    /**
     * Create a new Jersey HTTP client.
     *
     * @return A new Jersey 1.x {@link HttpClient} implementation.
     */
    @Override
    protected HttpClient createClientImplementation() {
        return new JerseyHttpClient()
    }
}
