package com.budjb.httprequests.converter

/**
 * An entity reader that converts an entity into a String. The character set of the entity is respected.
 */
class StringEntityReader implements EntityReader {
    /**
     * Determines if the reader supports converting an entity to the given class type.
     *
     * @param type Type to convert to.
     * @return Whether the type is supported.
     */
    @Override
    boolean support(Class<?> type) {
        return String.isAssignableFrom(type)
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter can attempt a conversion.
     *
     * @param entity Entity as a byte array.
     * @param contentType Content-Type of the entity.
     * @param charset Character set of the entity.
     * @return The converted entity.
     * @throws Exception when an unexpected error occurs during conversion.
     */
    @Override
    Object read(byte[] entity, String contentType, String charset) throws Exception {
        if (charset) {
            return new String(entity, charset)
        }
        else {
            return new String(entity)
        }
    }
}
