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
package com.budjb.httprequests.reference

import com.budjb.httprequests.AbstractHttpClientFactory
import com.budjb.httprequests.HttpClient
import com.budjb.httprequests.HttpClientFactory

/**
 * A built-in, basic implementation of an {@link HttpClientFactory}. This implementation is useful
 * when minimal external dependencies are desired.
 */
class ReferenceHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Base constructor that automatically registers the default set of entity converters.
     */
    ReferenceHttpClientFactory() {
        super()
    }

    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param registerDefaultConverters Whether to register the default set of entity converters.
     */
    ReferenceHttpClientFactory(boolean registerDefaultConverters) {
        super(registerDefaultConverters)
    }

    /**
     * Implementation factories should implement this method to create a concrete {@link HttpClient} instance specific
     * to the HTTP client implementation.
     *
     * @return A new {@link HttpClient} implementation instance.
     */
    @Override
    protected HttpClient createClientImplementation() {
        return new ReferenceHttpClient()
    }
}
