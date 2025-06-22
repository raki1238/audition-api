package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import brave.Tracer;
import brave.Tracing;
import com.audition.interceptor.ResponseHeaderInjector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = ResponseHeaderInjectorTest.SleuthConfig.class)
class ResponseHeaderInjectorTest {

    private transient Tracer tracer;
    private transient ResponseHeaderInjector injector;
    private transient HttpServletRequest request;
    private transient HttpServletResponse response;

    @TestConfiguration
    public static class SleuthConfig {

        @Bean
        @Primary
        public Tracer tracer() {
            return Tracing.newBuilder().build().tracer();
        }
    }

    @BeforeEach
    void setUp() {
        tracer = Tracing.newBuilder().build().tracer();
        injector = new ResponseHeaderInjector(tracer);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testPreHandleWithSpan() throws Exception {
        tracer.nextSpan().start();
        final boolean result = injector.preHandle(request, response, new Object());
        // You can only verify that headers are set if a span is present
        // (You may need to use a real HttpServletResponse for full integration)
        assertTrue(result);
    }

    @Test
    void testPreHandleWithoutSpan() throws Exception {
        final boolean result = injector.preHandle(request, response, new Object());
        // No span, so no headers set
        assertTrue(result);
    }
} 