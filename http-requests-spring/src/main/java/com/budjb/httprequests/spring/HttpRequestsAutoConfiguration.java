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
import com.budjb.httprequests.converter.EntityConverter;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.httpcomponents.client.HttpComponentsClientFactory;
import com.budjb.httprequests.jersey1.JerseyHttpClientFactory;
import com.budjb.httprequests.reference.ReferenceHttpClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class HttpRequestsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    EntityConverterManager entityConverterManager(Optional<List<EntityConverter>> entityConverters) {
        EntityConverterManager converterManager = new EntityConverterManager();
        entityConverters.ifPresent(list -> list.forEach(converterManager::add));
        return converterManager;
    }

    @Bean("httpClientFactory")
    @ConditionalOnClass(name = "com.budjb.httprequests.httpcomponents.client.HttpComponentsClientFactory")
    @ConditionalOnMissingBean
    HttpClientFactory apacheHttpClientFactory(EntityConverterManager converterManager) {
        return new HttpComponentsClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @ConditionalOnClass(name = "com.budjb.httprequests.jersey1.JerseyHttpClientFactory")
    @ConditionalOnMissingBean
    HttpClientFactory jersey1HttpClientFactory(EntityConverterManager converterManager) {
        return new JerseyHttpClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @ConditionalOnClass(name = "com.budjb.httprequests.jersey2.JerseyHttpClientFactory")
    @ConditionalOnMissingBean
    HttpClientFactory jersey2HttpClientFactory(EntityConverterManager converterManager) {
        return new com.budjb.httprequests.jersey2.JerseyHttpClientFactory(converterManager);
    }

    @Bean("httpClientFactory")
    @ConditionalOnMissingBean
    HttpClientFactory referenceHttpClientFactory(EntityConverterManager entityConverterManager) {
        return new ReferenceHttpClientFactory(entityConverterManager);
    }
}
