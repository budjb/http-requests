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

package com.budjb.httprequests.spring;

import com.budjb.httprequests.HttpClientFactory;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.httpcomponents.client.HttpComponentsClientFactory;
import com.budjb.httprequests.jersey1.JerseyHttpClientFactory;
import com.budjb.httprequests.reference.ReferenceHttpClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HttpClientFactoryConfiguration {
    @Bean("httpClientFactory")
    @Primary
    @ConditionalOnClass(name = "com.budjb.httprequests.httpcomponents.client.HttpComponentsClientFactory")
    @ConditionalOnMissingBean
    public HttpClientFactory apacheHttpClientFactory(EntityConverterManager converterManager) {
        return new HttpComponentsClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @Primary
    @ConditionalOnClass(name = "com.budjb.httprequests.jersey1.JerseyHttpClientFactory")
    @ConditionalOnMissingBean
    public HttpClientFactory jersey1HttpClientFactory(EntityConverterManager converterManager) {
        return new JerseyHttpClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @Primary
    @ConditionalOnClass(name = "com.budjb.httprequests.jersey2.JerseyHttpClientFactory")
    @ConditionalOnMissingBean
    public HttpClientFactory jersey2HttpClientFactory(EntityConverterManager converterManager) {
        return new com.budjb.httprequests.jersey2.JerseyHttpClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @ConditionalOnMissingBean
    public HttpClientFactory referenceHttpClientFactory(EntityConverterManager entityConverterManager) {
        return new ReferenceHttpClientFactory(entityConverterManager);
    }
}
