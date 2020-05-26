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

package com.budjb.httprequests.groovy;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.converter.EntityWriter;
import com.budjb.httprequests.converter.bundled.BuiltinEntityConverter;
import groovy.lang.GString;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

public class GStringEntityWriter extends BuiltinEntityConverter implements EntityWriter {
    /**
     * Default content type.
     */
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String characterSet) {
        return GString.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntity write(Object entity, String contentType, String characterSet) throws Exception {
        String charset = characterSet != null ? characterSet : Charset.defaultCharset().name();

        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
            characterSet = charset;
        }

        return new HttpEntity(
            new ByteArrayInputStream(entity.toString().getBytes(charset)),
            contentType,
            characterSet
        );
    }
}
