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

import com.budjb.httprequests.FormData;
import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.converter.EntityWriter;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An entity writer that formats form data.
 */
public class FormDataEntityWriter extends BuiltinEntityConverter implements EntityWriter {
    /**
     * Default content type.
     */
    private final static String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type, String contentType, String characterSet) {
        return FormData.class.isAssignableFrom(type);
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

        List<String> parts = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : ((FormData) entity).getFields().entrySet()) {
            String k = URLEncoder.encode(entry.getKey(), characterSet);

            for (String value : entry.getValue()) {
                parts.add(k + "=" + URLEncoder.encode(value, characterSet));
            }
        }

        return new HttpEntity(
            new ByteArrayInputStream(String.join("&", parts).getBytes(characterSet)),
            contentType,
            characterSet
        );
    }
}
