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
package com.budjb.httprequests.exception

import com.budjb.httprequests.HttpResponse

/**
 * An exception representing a non-2XX HTTP response. Contains both the
 * HTTP status of the response and the response object itself.
 */
class HttpStatusException extends RuntimeException {
    /**
     * HTTP statuses mapped to exception types.
     */
    protected static final Map<Integer, Class<? extends HttpStatusException>> httpStatusCodes = [
        300: HttpMultipleChoicesException,
        301: HttpMovedPermanentlyException,
        302: HttpFoundException,
        303: HttpSeeOtherException,
        304: HttpNotModifiedException,
        305: HttpUseProxyException,
        307: HttpTemporaryRedirectException,
        400: HttpBadRequestException,
        401: HttpUnauthorizedException,
        402: HttpPaymentRequiredException,
        403: HttpForbiddenException,
        404: HttpNotFoundException,
        405: HttpMethodNotAllowedException,
        406: HttpNotAcceptableException,
        407: HttpProxyAuthenticationRequiredException,
        408: HttpRequestTimeoutException,
        409: HttpConflictException,
        410: HttpGoneException,
        411: HttpLengthRequiredException,
        412: HttpPreconditionFailedException,
        413: HttpRequestEntityTooLargeException,
        414: HttpRequestUriTooLongException,
        415: HttpUnsupportedMediaTypeException,
        416: HttpRequestedRangeNotSatisfiableException,
        417: HttpExpectationFailedException,
        422: HttpUnprocessableEntityException,
        500: HttpInternalServerErrorException,
        501: HttpNotImplementedException,
        502: HttpBadGatewayException,
        503: HttpServiceUnavailableException,
        504: HttpGatewayTimeoutException,
        505: HttpHttpVersionNotSupportedException
    ]

    /**
     * The HTTP status of the response.
     */
    int status

    /**
     * Response of the request.
     */
    HttpResponse response

    /**
     * Constructor.
     *
     * @param httpResponse Response properties of the HTTP request.
     */
    HttpStatusException(HttpResponse httpResponse) {
        super("the HTTP request returned HTTP status ${httpResponse.getStatus()}")

        response = httpResponse
        status = httpResponse.getStatus()
    }

    /**
     * Returns the proper exception type for the given HTTP status code.
     *
     * @param response Response properties of the request.
     * @return An appropriate subclass of the <code>HttpStatusException</code> for the request's status code.
     */
    static HttpStatusException build(HttpResponse response) {
        if (!httpStatusCodes.containsKey(response.getStatus())) {
            return new HttpStatusException(response)
        }
        return httpStatusCodes[response.getStatus()].newInstance(response) as HttpStatusException
    }
}
