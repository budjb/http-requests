package com.budjb.httprequests.spring;

import com.budjb.httprequests.filter.jackson.JacksonListReader;
import com.budjb.httprequests.filter.jackson.JacksonMapListWriter;
import com.budjb.httprequests.filter.jackson.JacksonMapReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ObjectMapper.class, JacksonMapListWriter.class, JacksonListReader.class, JacksonMapReader.class})
public class JacksonEntityConverterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JacksonMapListWriter jacksonMapListWriter(ObjectMapper objectMapper) {
        return new JacksonMapListWriter(objectMapper);
    }

    @Bean
    public JacksonMapReader jacksonMapReader(ObjectMapper objectMapper) {
        return new JacksonMapReader(objectMapper);
    }

    @Bean
    public JacksonListReader jacksonListReader(ObjectMapper objectMapper) {
        return new JacksonListReader(objectMapper);
    }
}
