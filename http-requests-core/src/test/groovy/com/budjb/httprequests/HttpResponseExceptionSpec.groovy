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
package com.budjb.httprequests

import com.budjb.httprequests.converter.EntityConverterManager
import com.budjb.httprequests.exception.*
import com.budjb.httprequests.mock.MockHttpResponse
import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseExceptionSpec extends Specification {
    @Unroll
    def 'When an HttpResponseException is build with status #status, exception type #type is returned'() {
        setup:
        HttpResponse response = new MockHttpResponse(
            new EntityConverterManager([]),
            new HttpRequest(),
            status,
            new MultiValuedMap(),
            null
        )

        expect:
        HttpStatusException.build(response).getClass() == type

        where:
        status | type
        300    | HttpMultipleChoicesException
        301    | HttpMovedPermanentlyException
        302    | HttpFoundException
        303    | HttpSeeOtherException
        304    | HttpNotModifiedException
        305    | HttpUseProxyException
        307    | HttpTemporaryRedirectException
        400    | HttpBadRequestException
        401    | HttpUnauthorizedException
        402    | HttpPaymentRequiredException
        403    | HttpForbiddenException
        404    | HttpNotFoundException
        405    | HttpMethodNotAllowedException
        406    | HttpNotAcceptableException
        407    | HttpProxyAuthenticationRequiredException
        408    | HttpRequestTimeoutException
        409    | HttpConflictException
        410    | HttpGoneException
        411    | HttpLengthRequiredException
        412    | HttpPreconditionFailedException
        413    | HttpRequestEntityTooLargeException
        414    | HttpRequestUriTooLongException
        415    | HttpUnsupportedMediaTypeException
        416    | HttpRequestedRangeNotSatisfiableException
        417    | HttpExpectationFailedException
        422    | HttpUnprocessableEntityException
        500    | HttpInternalServerErrorException
        501    | HttpNotImplementedException
        502    | HttpBadGatewayException
        503    | HttpServiceUnavailableException
        504    | HttpGatewayTimeoutException
        505    | HttpHttpVersionNotSupportedException

    }
}
