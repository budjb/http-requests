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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class JacksonEntityConverter {
    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Jackson object mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param objectMapper Jackson object mapper.
     */
    public JacksonEntityConverter(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "Jackson ObjectMapper must not be null");
    }

    /**
     * Returns the Jackson object mapper.
     *
     * @return Jack object mapper.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Logs a failure of Jackson to support a class type. If an underlying exception occurred
     * during Jackson's check, it is also logged.
     *
     * @param action Text action to place in the log message.
     * @param type   Class type that was being checked.
     * @param cause  Underlying exception thrown while determining Jackson support.
     */
    protected void logFailure(String action, Class<?> type, Throwable cause) {
        if (cause != null) {
            log.trace("Jackson does not support " + action + " class type " + type.getName(), cause);
        }
        else {
            log.trace("Jackson does not support " + action + " class type " + type.getName());
        }
    }
}
