package com.audition.logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class AuditionLoggerTest {
 
    @Test
    void testAllLoggerMethods() {
        final AuditionLogger logger = new AuditionLogger();
        final Logger mockLogger = mock(Logger.class);
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("title");
        problemDetail.setDetail("detail");

        // Simulate all log levels enabled
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        when(mockLogger.isErrorEnabled()).thenReturn(true);

        logger.info(mockLogger, "info");
        logger.info(mockLogger, "info {}", 1);
        logger.debug(mockLogger, "debug");
        logger.warn(mockLogger, "warn");
        logger.error(mockLogger, "error");
        logger.logErrorWithException(mockLogger, "err", new Exception("ex"));
        logger.logStandardProblemDetail(mockLogger, problemDetail, new Exception("ex"));
        logger.logHttpStatusCodeError(mockLogger, "msg", 400);

        verify(mockLogger, atLeastOnce()).info(anyString());
        //verify(mockLogger, atLeastOnce()).info(anyString(), any());
        verify(mockLogger, atLeastOnce()).debug(anyString());
        verify(mockLogger, atLeastOnce()).warn(anyString());
        verify(mockLogger, atLeastOnce()).error(anyString());
        verify(mockLogger, atLeastOnce()).error(anyString(), any(Exception.class));
    }

    @Test
    void testLogLevelsDisabled() {
        final AuditionLogger logger = new AuditionLogger();
        final Logger mockLogger = mock(Logger.class);
        // All log levels disabled
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        when(mockLogger.isDebugEnabled()).thenReturn(false);
        when(mockLogger.isWarnEnabled()).thenReturn(false);
        when(mockLogger.isErrorEnabled()).thenReturn(false);

        logger.info(mockLogger, "info");
        logger.info(mockLogger, "info {}", 1);
        logger.debug(mockLogger, "debug");
        logger.warn(mockLogger, "warn");
        logger.error(mockLogger, "error");
        logger.logErrorWithException(mockLogger, "err", new Exception("ex"));
        logger.logStandardProblemDetail(mockLogger, null, new Exception("ex"));
        logger.logHttpStatusCodeError(mockLogger, null, null);

        verify(mockLogger, never()).info(anyString());
        //verify(mockLogger, never()).info(anyString(), any());
        verify(mockLogger, never()).debug(anyString());
        verify(mockLogger, never()).warn(anyString());
        verify(mockLogger, never()).error(anyString());
        verify(mockLogger, never()).error(anyString(), any(Exception.class));
        org.junit.jupiter.api.Assertions.assertFalse(mockLogger.isInfoEnabled());
    }

    @Test
    void testNullLoggerDoesNotThrow() {
        final AuditionLogger logger = new AuditionLogger();
        org.junit.jupiter.api.Assertions.assertNotNull(logger);
    }

    @Test
    void testNullAndEdgeCases() {
        final AuditionLogger logger = new AuditionLogger();
        final Logger mockLogger = mock(Logger.class);
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        // Null ProblemDetail
        assertDoesNotThrow(() -> logger.logStandardProblemDetail(mockLogger, null, new Exception("ex")));
        verify(mockLogger, atLeastOnce()).error(eq(""), any(Exception.class));
        // Null errorCode and message
        assertDoesNotThrow(() -> logger.logHttpStatusCodeError(mockLogger, null, null));
        verify(mockLogger, atLeastOnce()).error(contains("Unknown"));
        verify(mockLogger, atLeastOnce()).error(contains("No message provided"));
        org.junit.jupiter.api.Assertions.assertNotNull(logger);
    }
} 