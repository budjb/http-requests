package com.budjb.httprequests

class HttpResponse {
    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Content type of the response.
     */
    String contentType

    /**
     * Headers of the response.
     */
    Map<String, List<String>> headers

    /**
     * Entity of the response.
     */
    byte[] entity

    /**
     * The charater set of the response.
     */
    String charset = 'UTF-8'

    /**
     * Sets the charset of the response.
     *
     * @param charset
     */
    void setCharset(String charset) {
        if (charset) {
            this.charset = charset
        }
    }

    /**
     * Return the entity as a string.
     *
     * @return
     */
    String getEntityAsString() {
        return new String(entity, charset)
    }
}
