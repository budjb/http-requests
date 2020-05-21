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

import com.budjb.httprequests.ConvertingHttpEntity;
import com.budjb.httprequests.HttpEntity;
import com.budjb.httprequests.Ordered;
import com.budjb.httprequests.exception.UnsupportedConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EntityConverterManager {
    /**
     * Empty entity converter manager.
     */
    public static final EntityConverterManager empty = new EntityConverterManager(Collections.emptyList());

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(EntityConverterManager.class);

    /**
     * List of registered entity converters.
     */
    private final List<EntityConverter> converters;

    /**
     * Creates an entity converter manager containing the provided list of converters.
     */
    public EntityConverterManager(List<EntityConverter> entityConverters) {
        Comparator<EntityConverter> comparator = (o1, o2) -> {
            int l = (o1 instanceof Ordered) ? ((Ordered) o1).getOrder() : 0;
            int r = (o2 instanceof Ordered) ? ((Ordered) o2).getOrder() : 0;

            return Integer.compare(r, l);
        };

        converters = entityConverters.stream().sorted(comparator).collect(Collectors.toList());
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
     * Attempts to convert the given entity into an {@link InputStream}.
     *
     * @param entity Entity object to convert.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public HttpEntity write(Object entity) throws UnsupportedConversionException {
        return write(entity, null);
    }

    /**
     * Attempts to convert the given entity into an {@link InputStream} with the given content type.
     *
     * @param entity      Entity object to convert.
     * @param contentType Content type of the entity.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public HttpEntity write(Object entity, String contentType) throws UnsupportedConversionException {
        return write(entity, contentType, null);
    }

    /**
     * Attempts to convert the given entity into an {@link InputStream}.
     *
     * @param entity Converting HTTP entity.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public HttpEntity write(ConvertingHttpEntity entity) throws UnsupportedConversionException {
        return write(entity.getObject(), entity.getContentType(), entity.getCharSet());
    }

    /**
     * Attempts to convert the given entity into an {@link InputStream} with the given content type and character set.
     *
     * @param entity       Entity object to convert.
     * @param contentType  Content type of the entity.
     * @param characterSet Character set of the entity.
     * @return Converted entity as an InputStream.
     * @throws UnsupportedConversionException when there are no entity writers that support the object type.
     */
    public HttpEntity write(Object entity, String contentType, String characterSet) throws UnsupportedConversionException {
        if (entity == null) {
            return null;
        }

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
