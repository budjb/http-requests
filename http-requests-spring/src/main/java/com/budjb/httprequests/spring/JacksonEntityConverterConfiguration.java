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

import com.budjb.httprequests.filter.jackson.JacksonListReader;
import com.budjb.httprequests.filter.jackson.JacksonListWriter;
import com.budjb.httprequests.filter.jackson.JacksonMapReader;
import com.budjb.httprequests.filter.jackson.JacksonMapWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ObjectMapper.class, JacksonMapWriter.class, JacksonListReader.class, JacksonMapReader.class})
public class JacksonEntityConverterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JacksonMapWriter jacksonMapWriter(ObjectMapper objectMapper) {
        return new JacksonMapWriter(objectMapper);
    }

    @Bean
    public JacksonListWriter jacksonListWriter(ObjectMapper objectMapper) {
        return new JacksonListWriter(objectMapper);
    }

    @Bean
    public JacksonMapReader jacksonMapReader(ObjectMapper objectMapper) {
        return new JacksonMapReader(objectMapper);
    }

    @Bean
    public JacksonListReader jacksonListReader(ObjectMapper objectMapper) {
        return new JacksonListReader(objectMapper);
    }
}
