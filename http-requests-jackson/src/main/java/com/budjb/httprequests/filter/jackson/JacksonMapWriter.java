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

package com.budjb.httprequests.filter.jackson;

import com.budjb.httprequests.converter.EntityWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * A map JSON writer that utilizes Jackson.
 */
public class JacksonMapWriter implements EntityWriter {
    /**
     * Jackson object mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param objectMapper Jackson object mapper.
     */
    public JacksonMapWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream write(Object entity, String characterSet) throws Exception {
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(entity));
    }
}
