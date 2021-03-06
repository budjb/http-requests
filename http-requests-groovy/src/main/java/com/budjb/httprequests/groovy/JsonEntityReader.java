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

import com.budjb.httprequests.converter.EntityReader;
import groovy.json.JsonSlurper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class JsonEntityReader implements EntityReader {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> type) {
        return List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(InputStream entity, String contentType, String charset) {
        if (charset == null) {
            charset = Charset.defaultCharset().toString();
        }
        return new JsonSlurper().parse(entity, charset);
    }
}
