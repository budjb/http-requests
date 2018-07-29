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

import java.util.List;

/**
 * The {@code FormData} class contains a multi-valued map useful for submitting form elements with an HTTP request.
 */
public class FormData {
    /**
     * Map containing the values of the form.
     */
    private MultiValuedMap fields = new MultiValuedMap();

    /**
     * Adds a form field.
     *
     * @param name  Name of the form field.
     * @param value Value of the form field.
     * @return The same object the method was called on.
     */
    public FormData addField(String name, String value) {
        fields.add(name, value);
        return this;
    }

    /**
     * Adds a form field with multiple values.
     *
     * @param name   Name of the field to add.
     * @param values List of values to add to the field.
     * @return The same object the method was called on.
     */
    public FormData addField(String name, List<String> values) {
        fields.add(name, values);
        return this;
    }

    /**
     * Adds many elements to the form.
     *
     * @param data A map of fields to add, where the key is the name of the field and the value
     *             is either a <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The same object the method was called on.
     */
    public FormData addFields(MultiValuedMap data) {
        this.fields.add(data);
        return this;
    }

    /**
     * Return the form elements as a map, where the key is the name of the form field and the value is a list of values
     * for the form field, even if there is only one value.
     *
     * @return All fields in the object.
     */
    public MultiValuedMap getFields() {
        return fields;
    }
}
