package com.budjb.httprequests

import spock.lang.Specification
import spock.lang.Unroll

class HttpResponseSpec extends Specification {
    def 'When a charset is provided, the resulting string is built using it'() {
        setup:
        HttpResponse response = new HttpResponse()
        response.entity = 'åäö'.getBytes()
        response.charset = 'euc-jp'

        when:
        String entity = response.getEntityAsString()

        then:
        entity == '奪辰旦'
    }

    def 'When no charset is provided, UTF-8 is used'() {
        setup:
        HttpResponse response = new HttpResponse()
        response.entity = 'åäö'.getBytes()

        when:
        String entity = response.getEntityAsString()

        then:
        response.charset == 'UTF-8'
        entity == 'åäö'
    }

    @Unroll
    def 'When a #type charset is assigned, charset is not actually assigned'() {
        setup:
        HttpResponse response = new HttpResponse()

        when:
        response.charset = charset

        then:
        response.charset == 'UTF-8'

        where:
        type    | charset
        'null'  | null
        'blank' | ''
    }
}
