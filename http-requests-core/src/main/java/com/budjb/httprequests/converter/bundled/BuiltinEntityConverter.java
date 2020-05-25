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

package com.budjb.httprequests.converter.bundled;

import com.budjb.httprequests.Ordered;
import com.budjb.httprequests.converter.EntityConverter;

public abstract class BuiltinEntityConverter implements EntityConverter, Ordered {
    /**
     * The maximum possible priority for a built-in converter.
     */
    public int MAX_BUILTIN_PRIORITY = Ordered.LOWEST_PRIORITY + 200;

    /**
     * The default priority for a built-in converter.
     */
    public int DEFAULT_BUILTIN_PRIORITY = MAX_BUILTIN_PRIORITY - 50;

    /**
     * The lowest priority for a built-in converter.
     */
    public int MIN_BUILTIN_PRIORITY = MAX_BUILTIN_PRIORITY - 100;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return DEFAULT_BUILTIN_PRIORITY;
    }
}
