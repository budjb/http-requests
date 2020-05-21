/*
 * Copyright 2016-2020 the original author or authors.
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

import com.budjb.httprequests.Ordered;
import com.budjb.httprequests.converter.EntityReader;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public class JacksonEntityReader extends JacksonEntityConverter implements EntityReader, Ordered {
    /**
     * Constructor.
     *
     * @param objectMapper Jackson object mapper.
     */
    public JacksonEntityReader(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String charset) {
        AtomicReference<Throwable> causeRef = new AtomicReference<>();

        if (getObjectMapper().canSerialize(type, causeRef)) {
            return true;
        }

        logFailure("de-serializing", type, causeRef.get());

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Class<? extends T> clazz, InputStream entity, String contentType, String charset) throws Exception {
        TypeFactory typeFactory = getObjectMapper().getTypeFactory();
        JavaType javaType = typeFactory.constructType(clazz);
        return (T) getObjectMapper().readValue(entity, javaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRIORITY + 5;
    }
}
