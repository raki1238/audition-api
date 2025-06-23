package com.audition.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.interceptor.ResponseHeaderInjector;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * Test class to check if the Response Headers have the traceId and SpanId or not
 */

class ResponseHeaderInjectorTest {

    private transient Tracer tracer;
    private transient ResponseHeaderInjector injector;
    private transient HttpServletRequest request;
    private transient HttpServletResponse response;
    private transient FilterChain filterChain;
    private transient Span span;
    private transient TraceContext traceContext;

    @BeforeEach
    void setUp() {
        tracer = mock(Tracer.class);
        injector = new ResponseHeaderInjector(tracer);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        span = mock(Span.class);
        traceContext = mock(TraceContext.class);
    }

    @Test
    void testDoFilterWithSpan() throws Exception {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace-111");
        when(traceContext.spanId()).thenReturn("span-111");

        injector.doFilter(request, response, filterChain);

        verify(response).setHeader("X-Trace-Id", "trace-111");
        verify(response).setHeader("X-Span-Id", "span-111");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterWithoutSpan() throws Exception {
        when(tracer.currentSpan()).thenReturn(null);

        injector.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}