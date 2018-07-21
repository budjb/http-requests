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

package com.budjb.httprequests;

import java.util.*;

public class MultiValuedMap extends TreeMap<String, List<String>> {
    public MultiValuedMap() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    public MultiValuedMap(Map<String, List<String>> other) {
        this();
        putAll(other);
    }

    public void add(String key, String value) {
        if (!containsKey(key)) {
            put(key, new ArrayList<>());
        }
        get(key).add(value);
    }

    public void add(String key, List<String> values) {
        if (!containsKey(key)) {
            put(key, new ArrayList<>());
        }
        get(key).addAll(values);
    }

    public void add(Map<String, List<String>> other) {
        other.forEach((k, v) -> {
            if (containsKey(k)) {
                get(k).addAll(v);
            }
            else {
                put(k, v);
            }
        });
    }

    public void set(String key, String value) {
        put(key, Arrays.asList(value));
    }

    public void set(String key, List<String> values) {
        put(key, values);
    }

    public void set(Map<String, List<String>> other) {
        clear();
        other.forEach(this::put);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        MultiValuedMap map = (MultiValuedMap) super.clone();

        this.forEach((k, v) -> {
            map.put(k, new ArrayList<>(get(k)));
        });

        return map;
    }

    public String getFlat(String key) {
        if (!containsKey(key)) {
            return null;
        }

        return String.join(",", get(key));
    }

    @Override
    public void clear() {
        super.clear();
    }
}
