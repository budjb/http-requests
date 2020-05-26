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

public class ByteArrayEntityWriter extends BuiltinEntityConverter implements EntityWriter {
    /**
     * Default content type.
     */
    private final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String characterSet) {
        return type.isArray() && byte.class.isAssignableFrom(type.getComponentType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntity write(Object entity, String contentType, String characterSet) throws Exception {
        return new HttpEntity(
            new ByteArrayInputStream((byte[]) entity),
            contentType != null ? contentType : DEFAULT_CONTENT_TYPE,
            characterSet
        );
    }
}
