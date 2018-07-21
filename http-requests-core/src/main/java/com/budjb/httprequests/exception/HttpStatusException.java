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
package com.budjb.httprequests.exception;

import com.budjb.httprequests.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * An exception representing a non-2XX HTTP response. Contains both the
 * HTTP status of the response and the response object itself.
 */
public class HttpStatusException extends RuntimeException {
    /**
     * HTTP statuses mapped to exception types.
     */
    private static final Map<Integer, Class<? extends HttpStatusException>> httpStatusCodes;

    static {
        httpStatusCodes = new HashMap<>();
        httpStatusCodes.put(300, HttpMultipleChoicesException.class);
        httpStatusCodes.put(301, HttpMovedPermanentlyException.class);
        httpStatusCodes.put(302, HttpFoundException.class);
        httpStatusCodes.put(303, HttpSeeOtherException.class);
        httpStatusCodes.put(304, HttpNotModifiedException.class);
        httpStatusCodes.put(305, HttpUseProxyException.class);
        httpStatusCodes.put(307, HttpTemporaryRedirectException.class);
        httpStatusCodes.put(400, HttpBadRequestException.class);
        httpStatusCodes.put(401, HttpUnauthorizedException.class);
        httpStatusCodes.put(402, HttpPaymentRequiredException.class);
        httpStatusCodes.put(403, HttpForbiddenException.class);
        httpStatusCodes.put(404, HttpNotFoundException.class);
        httpStatusCodes.put(405, HttpMethodNotAllowedException.class);
        httpStatusCodes.put(406, HttpNotAcceptableException.class);
        httpStatusCodes.put(407, HttpProxyAuthenticationRequiredException.class);
        httpStatusCodes.put(408, HttpRequestTimeoutException.class);
        httpStatusCodes.put(409, HttpConflictException.class);
        httpStatusCodes.put(410, HttpGoneException.class);
        httpStatusCodes.put(411, HttpLengthRequiredException.class);
        httpStatusCodes.put(412, HttpPreconditionFailedException.class);
        httpStatusCodes.put(413, HttpRequestEntityTooLargeException.class);
        httpStatusCodes.put(414, HttpRequestUriTooLongException.class);
        httpStatusCodes.put(415, HttpUnsupportedMediaTypeException.class);
        httpStatusCodes.put(416, HttpRequestedRangeNotSatisfiableException.class);
        httpStatusCodes.put(417, HttpExpectationFailedException.class);
        httpStatusCodes.put(422, HttpUnprocessableEntityException.class);
        httpStatusCodes.put(500, HttpInternalServerErrorException.class);
        httpStatusCodes.put(501, HttpNotImplementedException.class);
        httpStatusCodes.put(502, HttpBadGatewayException.class);
        httpStatusCodes.put(503, HttpServiceUnavailableException.class);
        httpStatusCodes.put(504, HttpGatewayTimeoutException.class);
        httpStatusCodes.put(505, HttpHttpVersionNotSupportedException.class);
    }

    /**
     * The HTTP status of the response.
     */
    private final int status;

    /**
     * Response to the request.
     */
    private final HttpResponse response;

    /**
     * Constructor.
     *
     * @param httpResponse Response properties of the HTTP request.
     */

    public HttpStatusException(HttpResponse httpResponse) {
        super("the HTTP request returned HTTP status " + httpResponse.getStatus());

        response = httpResponse;
        status = httpResponse.getStatus();
    }

    /**
     * Returns the proper exception type for the given HTTP status code.
     *
     * @param response Response properties of the request.
     * @return An appropriate subclass of the <code>HttpStatusException</code> for the request's status code.
     */
    public static HttpStatusException build(HttpResponse response) {
        if (!httpStatusCodes.containsKey(response.getStatus())) {
            return new HttpStatusException(response);
        }
        try {
            return httpStatusCodes.get(response.getStatus()).getConstructor(HttpResponse.class).newInstance(response);
        }
        catch (Exception e) {
            throw new RuntimeException("unexpected error constructing an HttpStatusException", e);
        }
    }

    /**
     * Returns the HTTP status of the response.
     *
     * @return The HTTP status of the response.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the response to the request.
     *
     * @return The response to the request.
     */
    public HttpResponse getResponse() {
        return response;
    }
}
