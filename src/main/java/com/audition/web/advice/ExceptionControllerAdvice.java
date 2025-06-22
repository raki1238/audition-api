package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("PMD.GuardLogStatement")
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String ERROR_MESSAGE = " Error Code from Exception could not be mapped to a valid HttpStatus Code - ";
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";

    private AuditionLogger logger;

    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        return createProblemDetail(e, e.getStatusCode());

    }


    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception e) {
        final HttpStatusCode status = getHttpStatusCodeFromException(e);
        return createProblemDetail(e, status);

    }

    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        final HttpStatusCode status = getHttpStatusCodeFromSystemException(e);
        return createProblemDetail(e, status);

    }

    // Method to handle any validation errors, like postId is not null
    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolationException(final ConstraintViolationException exception) {
        logger.logErrorWithException(LOG, "Constraint violation exception occurred", exception);

        final String errorMessage = exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        final ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Validation Error");
        problemDetail.setDetail(errorMessage);

        return problemDetail;
    }

    // Method to handle any type conversion errors like converting String to Int of postId
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        logger.logErrorWithException(LOG, "Method argument TypeMismatch exception occurred", exception);
        final String param = exception.getName();
        final String value = String.valueOf(exception.getValue());
        final String expected =
            exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName().toLowerCase(Locale.ROOT)
                : "value";

        final ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Invalid Parameter");
        problemDetail.setDetail(
            String.format("Parameter '%s' must be a valid %s. Received: '%s'", param, expected, value));

        return problemDetail;
    }

    private ProblemDetail createProblemDetail(final Exception exception,
        final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(exception));
        if (exception instanceof SystemException) {
            problemDetail.setTitle(((SystemException) exception).getTitle());
        } else {
            problemDetail.setTitle(DEFAULT_TITLE);
        }
        logger.logStandardProblemDetail(LOG, problemDetail, exception);
        return problemDetail;
    }

    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (final IllegalArgumentException iae) {
            logger.info(LOG, ERROR_MESSAGE + exception.getStatusCode());
            return INTERNAL_SERVER_ERROR;
        }
    }

    // Handle the HttpClient errors like Method Not Allowed.
    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            return ((HttpClientErrorException) exception).getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}



