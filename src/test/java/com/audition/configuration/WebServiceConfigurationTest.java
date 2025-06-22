package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.*;

import com.audition.common.logging.AuditionLogger;
import com.audition.interceptor.ResponseHeaderInjector;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

class WebServiceConfigurationTest {

    private transient WebServiceConfiguration config;
    private transient AuditionLogger auditionLogger;


    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger();
        final ResponseHeaderInjector responseHeaderInjector = new ResponseHeaderInjector(null);
        config = new WebServiceConfiguration(responseHeaderInjector, auditionLogger);
    }

    @Test
    void testObjectMapperConfiguration() {
        final ObjectMapper mapper = config.objectMapper();
        assertNotNull(mapper);
        assertEquals("yyyy-MM-dd", ((SimpleDateFormat) mapper.getDateFormat()).toPattern());
        assertFalse(mapper.getSerializationConfig().isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertEquals(com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE, mapper.getPropertyNamingStrategy());
        assertEquals(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY, mapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    void testMappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter converter = config.mappingJackson2HttpMessageConverter();
        assertNotNull(converter);
        assertNotNull(converter.getObjectMapper());
    }

    @Test
    void testRestTemplateConfiguration() {
        final RestTemplate restTemplate = config.restTemplate();
        assertNotNull(restTemplate);
        assertFalse(restTemplate.getInterceptors().isEmpty());
        assertTrue(restTemplate.getMessageConverters().stream().anyMatch(c -> c instanceof MappingJackson2HttpMessageConverter));
    }

    @Test
    void testRestClientLogInterceptorBean() {
        assertNotNull(config.restClientLogInterceptor(auditionLogger, config.objectMapper()));
    }
} 