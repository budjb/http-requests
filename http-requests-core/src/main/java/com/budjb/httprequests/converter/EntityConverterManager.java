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
package com.budjb.httprequests.converter;

import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.exception.UnsupportedConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityConverterManager {
    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(EntityConverterManager.class);

    /**
     * List of registered entity converters.
     */
    private final List<EntityConverter> converters = new ArrayList<>();

    /**
     * Base constructor.
     */
    public EntityConverterManager() {
    }

    /**
     * Creates a converter manager with the contents of another manager.
     *
     * @param other Other converter manager to make a copy of.
     */
    public EntityConverterManager(EntityConverterManager other) {
        other.getAll().forEach(this::add);
    }

    /**
     * Adds an entity converter to the manager.
     *
     * @param converter Converter to add to the manager.
     */
    public void add(EntityConverter converter) {
        converters.add(converter);
    }

    /**
     * Returns the list of entity converters.
     *
     * @return List of entity converters.
     */
    public List<EntityConverter> getAll() {
        return converters;
    }

    /**
     * Remove an entity converter.
     *
     * @param converter Entity converter to remove.
     */
    public void remove(EntityConverter converter) {
        converters.remove(converter);
    }

    /**
     * Remove all entity converters.
     */
    public void clear() {
        converters.clear();
    }

    /**
     * Returns the list of all registered entity readers.
     *
     * @return The list of all registered entity readers.
     */
    public List<EntityReader> getEntityReaders() {
        return converters.stream().filter(c -> c instanceof EntityReader).map(c -> (EntityReader) c).collect(Collectors.toList());
    }

    /**
     * Returns the list of all registered entity writers.
     *
     * @return The list of all registered entity writers.
     */
    public List<EntityWriter> getEntityWriters() {
        return converters.stream().filter(c -> c instanceof EntityWriter).map(c -> (EntityWriter) c).collect(Collectors.toList());
    }

    /**
     * Attempts to convert the given entity into an {@link InputStream}. If an entity writer is successful,
     * the content type for the conversion is set in the request, if a content type is available.
     *
     * @param entity       Entity object to convert.
     * @param contentType  Content type of the entity.
     * @param characterSet Character set of the entity.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public HttpEntity write(Object entity, String contentType, String characterSet) throws UnsupportedConversionException {
        Class<?> type = entity.getClass();

        for (EntityWriter writer : getEntityWriters()) {
            if (writer.supports(type)) {
                try {
                    InputStream inputStream = writer.write(entity, characterSet);

                    if (inputStream == null) {
                        continue;
                    }

                    if (contentType == null) {
                        contentType = writer.getContentType();
                    }

                    return new HttpEntity(inputStream, contentType, characterSet);
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityWriter " + writer.getClass().getName(), e);
                }
            }
        }

        throw new UnsupportedConversionException(type);
    }

    /**
     * Reads an object from the given entity {@link InputStream}.
     *
     * @param type   Object type to attempt conversion to.
     * @param entity Entity input stream.
     * @param <T>    Generic type of the method call.
     * @return The converted object.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     * @throws IOException                    When an IO exception occurs.
     */
    @SuppressWarnings("unchecked")
    public <T> T read(Class<?> type, HttpEntity entity) throws UnsupportedConversionException, IOException {
        InputStream inputStream = entity.getInputStream();

        for (EntityReader reader : getEntityReaders()) {
            if (reader.supports(type)) {
                try {
                    T object = (T) reader.read(inputStream, entity.getContentType(), entity.getCharSet());

                    if (object != null) {
                        return object;
                    }
                }
                catch (IOException e) {
                    throw e;
                }
                catch (Exception e) {
                    log.trace("error occurred during conversion with EntityReader " + reader.getClass(), e);
                }
            }
        }

        throw new UnsupportedConversionException(type);
    }
}
