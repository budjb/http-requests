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
package com.budjb.httprequests.jersey2;

import com.budjb.httprequests.AbstractHttpClientFactory;
import com.budjb.httprequests.HttpClient;
import com.budjb.httprequests.converter.EntityConverterManager;

public class JerseyHttpClientFactory extends AbstractHttpClientFactory {
    /**
     * Constructor that can optionally register the default set of entity converters.
     *
     * @param converterManager Entity converter manager.
     */
    public JerseyHttpClientFactory(EntityConverterManager converterManager) {
        super(converterManager);
    }

    /**
     * Create a new HTTP client.
     *
     * @return An {@link HttpClient} implementation instance.
     */
    @Override
    public HttpClient createHttpClient() {
        return new JerseyHttpClient(getConverterManager());
    }
}
