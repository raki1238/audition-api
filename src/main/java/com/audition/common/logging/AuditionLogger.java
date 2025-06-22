package com.audition.common.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

/*
 * AuditionLogger class is used to log any kind of log messages based on the log level set to the
 * package, class or app level.
 */

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {

        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {

        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {

        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {

        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {

        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {

        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {

        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {

        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message) + "\n");
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        if (standardProblemDetail == null) {
            return StringUtils.EMPTY;
        }

        final StringBuilder message = new StringBuilder(80);
        message.append("Problem Detail - Status: ")
            .append(standardProblemDetail.getStatus())
            .append(", Title: ").append(standardProblemDetail.getTitle())
            .append(", Detail: ").append(standardProblemDetail.getDetail());

        // Consider appending any other properties
        if (standardProblemDetail.getProperties() != null) {
            standardProblemDetail.getProperties().forEach((key, value) ->
                message.append(", ").append(key).append(", ").append(value));
        }

        return message.toString();
    }

    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {

        return "Error Response - "
            + "Error Code: " + (errorCode != null ? errorCode : "Unknown") + " - "
            + "Message: " + (message != null ? message : "No message provided");
    }
}
