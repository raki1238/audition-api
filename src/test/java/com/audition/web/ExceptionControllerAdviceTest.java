package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.web.advice.ExceptionControllerAdvice;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class ExceptionControllerAdviceTest {

    private transient ExceptionControllerAdvice advice;
    private final transient AuditionLogger logger = new AuditionLogger();
    private static final String PMD_AVOID_ACCESSIBILITY = "PMD.AvoidAccessibilityAlteration";

    @SuppressWarnings(PMD_AVOID_ACCESSIBILITY)
    @BeforeEach
    void setUp() throws Exception {
        advice = new ExceptionControllerAdvice(new AuditionLogger());
        final Field loggerField = ExceptionControllerAdvice.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(advice, logger);
    }

    @Test
    @SuppressWarnings(PMD_AVOID_ACCESSIBILITY)
    void testHandleHttpClientException() throws Exception {
        final HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not found");
        final Method m = ExceptionControllerAdvice.class.getDeclaredMethod("handleHttpClientException",
            HttpClientErrorException.class);
        m.setAccessible(true);
        final ProblemDetail detail = (ProblemDetail) m.invoke(advice, ex);
        assertEquals(404, detail.getStatus());
        assertTrue(detail.getDetail().contains("Not found"));
    }

    @Test
    @SuppressWarnings(PMD_AVOID_ACCESSIBILITY)
    void testHandleSystemException() throws Exception {
        final SystemException ex = new SystemException("detail", "title", 400);
        final Method m = ExceptionControllerAdvice.class.getDeclaredMethod("handleSystemException",
            SystemException.class);
        m.setAccessible(true);
        final ProblemDetail detail = (ProblemDetail) m.invoke(advice, ex);
        assertEquals(400, detail.getStatus());
        assertEquals("title", detail.getTitle());
        assertEquals("detail", detail.getDetail());
    }

    @Test
    void testHandleMainException() throws Exception {
        final Exception ex = new Exception("main error");
        final Method m = ExceptionControllerAdvice.class.getDeclaredMethod("handleMainException", Exception.class);
        m.setAccessible(true);
        final ProblemDetail detail = (ProblemDetail) m.invoke(advice, ex);
        assertEquals(500, detail.getStatus());
        assertEquals("API Error Occurred", detail.getTitle());
        assertEquals("main error", detail.getDetail());
    }

    @Test
    @SuppressWarnings(PMD_AVOID_ACCESSIBILITY)
    void testHandleConstraintViolationException() throws Exception {
        final ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("bad value");
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        final Method m = ExceptionControllerAdvice.class.getDeclaredMethod("handleConstraintViolationException",
            ConstraintViolationException.class);
        m.setAccessible(true);
        final ProblemDetail detail = (ProblemDetail) m.invoke(advice, ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Validation Error", detail.getTitle());
        assertEquals("bad value", detail.getDetail());
    }

    @Test
    void testHandleTypeMismatch() {
        final MethodParameter methodParameter = mock(MethodParameter.class);
        final MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "value", String.class, "param", methodParameter, new IllegalArgumentException("bad type"));
        final ProblemDetail detail = advice.handleTypeMismatch(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Invalid Parameter", detail.getTitle());
        assertTrue(detail.getDetail().contains("param"));
    }
} 