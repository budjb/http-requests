package com.budjb.httprequests.application

import groovy.json.JsonBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletResponse

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

    @RequestMapping(value = '/testBasicGet', method = RequestMethod.GET, produces = 'text/plain')
    String testBasicGet() {
        return "The quick brown fox jumps over the lazy dog."
    }

    @RequestMapping(value = '/testBasicPost', method = RequestMethod.POST, produces = 'text/plain')
    String testBasicPost(String entity) {
        return entity
    }

    @RequestMapping(value = '/testBasicPut', method = RequestMethod.PUT, produces = 'text/plain')
    String testBasicPut(String entity) {
        return entity
    }

    @RequestMapping(value = '/testBasicDelete', method = RequestMethod.DELETE, produces = 'text/plain')
    String testBasicDelete() {
        return "Please don't hurt me!"
    }

    @RequestMapping(value = '/testAccept', method = RequestMethod.GET)
    ResponseEntity<String> testAccept(@RequestHeader('Accept') accept) {
        String body
        String contentType
        int status = 200

        switch (accept) {
            case "application/json":
                body = '{"foo":"bar"}'
                contentType = 'application/json'
                break

            case "text/plain":
                body = 'I am plain text.'
                contentType = 'text/plain'
                break

            case "text/xml":
                body = '<foo>bar</foo>'
                contentType = 'text/xml'
                break

            default:
                body = "I can't handle ${accept}"
                contentType = 'text/plain'
                status = 406
        }

        HttpHeaders headers = new HttpHeaders()
        headers.add('Content-Type', contentType)

        return new ResponseEntity<String>(body, headers, HttpStatus.valueOf(status))
    }

    @RequestMapping(value = '/testReadTimeout', method = RequestMethod.GET, produces = 'text/plain')
    String testReadTimeout() {
        sleep(5000)
        return "Hello, world!"
    }

    @RequestMapping(value = '/testRedirect', method = RequestMethod.GET)
    void testRedirect(HttpServletResponse response) {
        response.sendRedirect('testBasicGet')
    }

    @RequestMapping(value = '/testParams', produces = 'application/json')
    String testParams(@RequestParam MultiValueMap<String, String> params) {
        return new JsonBuilder(params).toString()
    }

    @RequestMapping(value = '/testHeaders', produces = 'application/json')
    String testHeaders(@RequestHeader MultiValueMap<String, String> headers) {
        return new JsonBuilder(headers).toString()
    }

    @RequestMapping(value = '/test500', method = RequestMethod.GET)
    void test500(HttpServletResponse response) {
        response.getWriter().write('something bad happened')
        response.setStatus(500)
    }

    @RequestMapping(value = '/testAuth', method = RequestMethod.GET, produces = 'text/plain')
    void testAuth(@RequestHeader('Authorization') authorization, HttpServletResponse response) {
        if (authorization) {
            response.getWriter().write('authentication required')
            response.setStatus(401)
        }
        else {
            response.getWriter().write('welcome')
        }
    }
}
