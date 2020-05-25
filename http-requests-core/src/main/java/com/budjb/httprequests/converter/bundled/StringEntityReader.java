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
package com.budjb.httprequests.converter.bundled;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.StreamUtils;
import com.budjb.httprequests.converter.EntityReader;

import java.nio.charset.Charset;

/**
 * An entity reader that converts an entity into a String. The character set of the entity is respected.
 */
public class StringEntityReader extends BuiltinEntityConverter implements EntityReader {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String charset) {
        return String.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Class<? extends T> clazz, HttpEntity entity) throws Exception {
        return (T) StreamUtils.readString(
            entity.getInputStream(),
            entity.getCharSet() != null ? entity.getCharSet() : Charset.defaultCharset().name()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return MIN_BUILTIN_PRIORITY + 20;
    }
}
