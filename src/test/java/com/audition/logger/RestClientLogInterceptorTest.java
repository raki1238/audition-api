package com.audition.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import com.audition.interceptor.RestClientLogInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

/*
Test class for RestClient logger interceptor
 */

@SuppressWarnings({"PMD.CloseResource", "PMD.LinguisticNaming"})
class RestClientLogInterceptorTest {

    private transient AuditionLogger auditionLogger;
    private transient ObjectMapper objectMapper;
    private transient RestClientLogInterceptor interceptor;
    private transient HttpRequest request;
    private transient ClientHttpRequestExecution execution;
    private transient ClientHttpResponse response;

    @BeforeEach
    void setUp() {
        auditionLogger = mock(AuditionLogger.class);
        objectMapper = mock(ObjectMapper.class);
        interceptor = new RestClientLogInterceptor(auditionLogger, objectMapper);
        request = mock(HttpRequest.class);
        execution = mock(ClientHttpRequestExecution.class);
        response = mock(ClientHttpResponse.class);
    }

    @Test
    void interceptInfoLogging() throws IOException {
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.getBody()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenReturn(new Object() {
            @Override
            public String toString() {
                return "jsonObj";
            }
        });

        final ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);
        assertEquals(response, result);
        verify(auditionLogger, atLeastOnce()).info(any(), anyString());
    }

    @Test
    void interceptDebugLogging() throws IOException {
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.CREATED);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream("{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8)));
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenReturn(new Object() {
            @Override
            public String toString() {
                return "jsonObj";
            }
        });

        final ClientHttpResponse result = interceptor.intercept(request, "body".getBytes(StandardCharsets.UTF_8),
            execution);
        assertEquals(response, result);
        verify(auditionLogger, atLeastOnce()).info(any(), anyString());
    }

    @Test
    void getSafeResponseBodyAsStringFallbackToRaw() throws IOException {
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenThrow(new IOException("fail"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(execution.execute(any(), any())).thenReturn(response);
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        final byte[] badJson = "not-json".getBytes(StandardCharsets.UTF_8);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(badJson));

        final ClientHttpResponse result = interceptor.intercept(request, new byte[0], execution);
        assertEquals(response, result);
        verify(auditionLogger, atLeastOnce()).info(any(), anyString());
    }
} 