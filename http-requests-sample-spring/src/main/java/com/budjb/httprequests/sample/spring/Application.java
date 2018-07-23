package com.budjb.httprequests.sample.spring;

import com.budjb.httprequests.HttpClientFactory;
import com.budjb.httprequests.HttpRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(HttpClientFactory httpClientFactory) {
        return args -> System.out.println(httpClientFactory.createHttpClient().get(new HttpRequest("https://reqres.in/api/users")).getEntity(String.class));
    }
}
