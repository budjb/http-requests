package com.budjb.httprequests.converter

interface EntityWriter extends EntityConverter {
    /**
     * Returns a Content-Type of the converted object that will be set in the HTTP request.
     *
     * If no Content-Type is known, null is returned.
     *
     * @return Content-Type of the converted object, or null if unknown.
     */
    String getContentType()

    /**
     * Determines whether the given class type is supported by the writer.
     *
     * @param type Type to convert.
     * @return Whether the type is supported.
     */
    boolean supports(Class<?> type)

    /**
     * Convert the given entity.
     *
     * If an error occurs, null may be returned so that another converter may attempt conversion.
     *
     * @param entity Entity object to convert into a byte array.
     * @param characterSet The character set of the request.
     * @return The converted object, or null if an error occurs.
     * @throws Exception when an unexpected error occurs.
     */
    byte[] write(Object entity, String characterSet) throws Exception
}
