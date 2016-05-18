package com.budjb.httprequests.httpcomponents.client

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse

/**
 * An {@link HttpResponse} implementation that wraps an Apache {@link CloseableHttpResponse}.
 */
class HttpComponentsResponse extends HttpResponse {
    /**
     * The response returned from the Apache HTTP client.
     */
    CloseableHttpResponse response

    /**
     * Constructor.
     *
     * @param request
     * @param response
     */
    HttpComponentsResponse(HttpRequest request, EntityConverterManager converterManager, CloseableHttpResponse response) {
        this.converterManager = converterManager
        this.request = request
        this.response = response

        setStatus(response.getStatusLine().getStatusCode())
        response.getAllHeaders().each {
            addHeader(it.getName(), it.getValue())
        }

        HttpEntity entity = response.getEntity()
        if (entity) {
            if (entity.getContentType()) {
                setContentType(entity.getContentType().getValue())
            }
            setEntity(entity.getContent())
            if (request.isBufferResponseEntity()) {
                close()
            }
        }
        else {
            close()
        }
    }

    /**
     * Closes the {@link InputStream} of the response and the underlying client.
     *
     * @throws IOException
     */
    @Override
    void close() throws IOException {
        super.close()
        response.close()
    }
}
