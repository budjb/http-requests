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
import com.budjb.httprequests.filter.HttpClientFilterManager;
import com.budjb.httprequests.reference.ReferenceHttpClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpRequestsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    EntityConverterManager entityConverterManager() {
        return new EntityConverterManager();
    }

    @Bean
    @ConditionalOnMissingBean
    HttpClientFilterManager httpClientFilterManager() {
        return new HttpClientFilterManager();
    }

    @Bean
    @ConditionalOnMissingBean
    HttpClientFactory httpClientFactory(EntityConverterManager entityConverterManager, HttpClientFilterManager httpClientFilterManager) {
        // TODO: load the correct one based on classpath presence
        return new ReferenceHttpClientFactory(entityConverterManager, httpClientFilterManager);
    }
}
