package com.budjb.httprequests

import com.budjb.httprequests.exception.*
import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseExceptionSpec extends Specification {
    @Unroll
    def 'When an HttpResponseException is build with status #status, exception type #type is returned'() {
        setup:
        HttpResponse response = new HttpResponse(new HttpRequest(), status, [:], null)

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
