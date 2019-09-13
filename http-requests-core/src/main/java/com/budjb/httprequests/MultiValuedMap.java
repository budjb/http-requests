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
    /**
     * Creates a new, empty multi-valued map.
     */
    public MultiValuedMap() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Creates a new multi-valued map with the contents of the other, given map.
     *
     * @param other Other map to use as the basis for this map.
     */
    public MultiValuedMap(Map<String, List<String>> other) {
        this();
        putAll(other);
    }

    /**
     * Adds the given value to the given key.
     * <p>
     * Note that this method does not protect against duplicate values.
     *
     * @param key   Key to add the given value to.
     * @param value Value to add to the given key.
     */
    public void add(String key, String value) {
        if (!containsKey(key)) {
            put(key, new ArrayList<>());
        }

        if (value != null) {
            get(key).add(value);
        }
    }

    /**
     * Adds the given values to the given key.
     * <p>
     * Note that this method does not protect against duplicate values.
     *
     * @param key    Key to add the given values to.
     * @param values Values to add to the given key.
     */
    public void add(String key, List<String> values) {
        if (!containsKey(key)) {
            put(key, new ArrayList<>());
        }

        if (values != null) {
            get(key).addAll(values);
        }
    }

    /**
     * Adds the contents of the given map to the existing contents of this map.
     * <p>
     * Note that this method does not protect against duplicate values.
     *
     * @param other Other map to merge into this map.
     */
    public void add(Map<String, List<String>> other) {
        if (other != null) {
            other.forEach((k, v) -> {
                if (containsKey(k)) {
                    get(k).addAll(v);
                }
                else {
                    put(k, v);
                }
            });
        }
    }

    /**
     * Sets and overwrites the value of the given key.
     *
     * @param key   Key to set the value of.
     * @param value Value of the key.
     */
    public void set(String key, String value) {
        if (value == null) {
            put(key, new ArrayList<>());
        }
        else {
            put(key, Arrays.asList(value));
        }
    }

    /**
     * Sets and overwrites the values of the given key.
     *
     * @param key    Key to set the values of.
     * @param values Values of the key.
     */
    public void set(String key, List<String> values) {
        if (values == null) {
            put(key, new ArrayList<>());
        }
        else {
            put(key, values);
        }
    }

    /**
     * Replaces the contents of this map with those of the given map.
     *
     * @param other The map to replace the contents of this map with.
     */
    public void set(Map<String, List<String>> other) {
        Objects.requireNonNull(other);
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

    /**
     * Returns the flattened values of the given key, where each value is joined by a comma.
     *
     * @param key Key to retrieve the flattened values of.
     * @return The flattened value.
     */
    public String getFlat(String key) {
        if (!containsKey(key)) {
            return null;
        }

        return String.join(",", get(key));
    }
}
