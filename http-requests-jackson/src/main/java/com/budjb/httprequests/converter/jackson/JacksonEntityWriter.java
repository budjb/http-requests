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

package com.budjb.httprequests.converter.jackson;

import com.budjb.httprequests.Ordered;
import com.budjb.httprequests.converter.EntityWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public class JacksonEntityWriter extends JacksonEntityConverter implements EntityWriter, Ordered {
    /**
     * Constructor.
     *
     * @param objectMapper Jackson object mapper.
     */
    public JacksonEntityWriter(ObjectMapper objectMapper) {
        super(objectMapper);
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
        AtomicReference<Throwable> causeRef = new AtomicReference<>();

        if (getObjectMapper().canSerialize(type, causeRef)) {
            return true;
        }

        logFailure("serializing", type, causeRef.get());

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream write(Object entity, String characterSet) throws Exception {
        return new ByteArrayInputStream(getObjectMapper().writeValueAsBytes(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRIORITY + 5;
    }
}
