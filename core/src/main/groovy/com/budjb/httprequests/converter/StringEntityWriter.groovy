package com.budjb.httprequests.converter

/**
 * An entity writer that converts a String into a byte array.
 */
class StringEntityWriter implements EntityWriter {
    /**
     * Returns a Content-Type of the converted object that will be set in the HTTP request.
     *
     * If no Content-Type is known, null is returned.
     *
     * @return Content-Type of the converted object, or null if unknown.
     */
    @Override
    String getContentType() {
        return 'text/plain'
    }

    /**
     * Determines whether the given class type is supported by the writer.
     *
     * @param type Type to convert.
     * @return Whether the type is supported.
     */
    @Override
    boolean supports(Class<?> type) {
        return String.isAssignableFrom(type)
    }

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter may attempt conversion.
     *
     * @param entity Entity object to convert into a byte array.
     * @return The converted object, or null if an error occurs.
     * @throws Exception when an unexpected error occurs.
     */
    @Override
    byte[] write(Object entity) throws Exception {
        return ((String)entity).getBytes()
    }
}
