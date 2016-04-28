/*
 * Copyright 2016 Bud Byrd
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
package com.budjb.httprequests

/**
 * The <code>FormData</code> class contains a multi-valued map useful for submitting form elements with an HTTP request.
 */
class FormData {
    /**
     * Map containing the values of the form.
     */
    private Map<String, List<String>> data = [:]

    /**
     * Adds a form field.
     *
     * @param name Name of the form field.
     * @param value Value of the form field.
     * @return The same object the method was called on.
     */
    FormData addField(String name, String value) {
        if (!data.containsKey(name) || !(data.get(name) instanceof List)) {
            data.put(name, [])
        }
        data.get(name).add(value)
        return this
    }

    /**
     * Adds a form field with multiple values.
     *
     * @param name Name of the field to add.
     * @param values List of values to add to the field.
     * @return The same object the method was called on.
     */
    FormData addField(String name, List<String> values) {
        values.each {
            addField(name, it)
        }
        return this
    }

    /**
     * Adds many elements to the form.
     *
     * @param data A map of fields to add, where the key is the name of the field and the value
     *             is either a <code>String</code> or a <code>List</code> of <code>String</code>s.
     * @return The same object the method was called on.
     */
    FormData addFields(Map<String, Object> data) {
        data.each { name, values ->
            if (values instanceof List) {
                addField(name, values)
            }
            else {
                addField(name, values.toString())
            }
        }
        return this
    }

    /**
     * Return the form elements as a map, where the key is the name of the form field and the value is a list of values
     * for the form field, even if there is only one value.
     *
     * @return All fields in the object.
     */
    Map<String, List<String>> getFields() {
        return data
    }

    /**
     * Return the form fields as map, where the key is the name of the form field and the value is either a String or
     * a List of Strings if the field has multiple values.
     *
     * @return All fields in the objects.
     */
    Map<String, Object> getFlattenedFields() {
        return data.collectEntries { name, values ->
            if (values.size() == 1) {
                return [(name): values.get(0)]
            }
            else {
                return [(name): values]
            }
        } as Map<String, Object>
    }
}
