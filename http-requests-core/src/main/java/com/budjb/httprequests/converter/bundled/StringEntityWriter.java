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
import com.budjb.httprequests.converter.EntityWriter;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * An entity writer that converts a String.
 */
public class StringEntityWriter extends BuiltinEntityConverter implements EntityWriter {
    /**
     * Default content-type.
     */
    private final static String DEFAULT_CONTENT_TYPE = "text/plain";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String characterSet) {
        return String.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntity write(Object entity, String contentType, String characterSet) throws Exception {
        if (characterSet == null) {
            characterSet = Charset.defaultCharset().name();
        }

        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        return new HttpEntity(new ByteArrayInputStream(((String) entity).getBytes(characterSet)), contentType, characterSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return MIN_BUILTIN_PRIORITY + 20;
    }
}
