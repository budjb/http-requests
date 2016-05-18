package com.budjb.httprequests.jersey1

import com.budjb.httprequests.HttpRequest
import com.budjb.httprequests.HttpResponse
import com.budjb.httprequests.converter.EntityConverterManager
import com.sun.jersey.api.client.ClientResponse

/**
 * An {@link HttpResponse} implementation that wraps a {@link ClientResponse}.
 */
class JerseyHttpResponse extends HttpResponse {
    /**
     * Jersey Client response.
     */
    ClientResponse response

    /**
     * Constructor.
     *
     * @param request Request properties used to make the request.
     * @param converterManager Converter manager.
     * @param response Jersey Client response.
     */
    JerseyHttpResponse(HttpRequest request, EntityConverterManager converterManager, ClientResponse response) {
        super(request, converterManager)

        this.response = response

        setStatus(response.getStatus())
        setHeaders(response.getHeaders())

        if (response.getType()) {
            setContentType(response.getType().toString())
        }

        if (response.hasEntity()) {
            setEntity(response.getEntityInputStream())
            if (request.isBufferResponseEntity()) {
                close()
            }
        }
        else {
            close()
        }
    }
}
