package com.budjb.httprequests.application

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
class TestApp {
    static void main(String[] args) throws Exception {
        SpringApplication.run(TestApp, args)
    }

    @RequestMapping(value = '/test', method = RequestMethod.GET, produces = 'application/json;charset=ISO-8859-8')
    Map index() {
        return [foo: 'bar']
    }
}
