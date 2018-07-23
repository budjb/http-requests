package com.budjb.httprequests.spring;

import com.budjb.httprequests.converter.EntityConverter;
import com.budjb.httprequests.converter.EntityConverterManager;
import com.budjb.httprequests.converter.bundled.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EntityConverterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public EntityConverterManager entityConverterManager(List<EntityConverter> entityConverters) {
        EntityConverterManager converterManager = new EntityConverterManager();
        entityConverters.forEach(converterManager::add);
        return converterManager;
    }

    @Bean
    public StringEntityWriter stringEntityWriter() {
        return new StringEntityWriter();
    }

    @Bean
    public StringEntityReader stringEntityReader() {
        return new StringEntityReader();
    }

    @Bean
    public ByteArrayEntityWriter byteArrayEntityWriter() {
        return new ByteArrayEntityWriter();
    }

    @Bean
    public ByteArrayEntityReader byteArrayEntityReader() {
        return new ByteArrayEntityReader();
    }

    @Bean
    public FormDataEntityWriter formDataEntityWriter() {
        return new FormDataEntityWriter();
    }
}
