package com.audition.interceptor;

import com.audition.common.logging.AuditionLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/*
 * This class is used to print all the Requests and Responses of the APIs being called
 * from our APIs to third party APIs.
 */

@RequiredArgsConstructor
@SuppressWarnings("PMD.GuardLogStatement")
public class RestClientLogInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientLogInterceptor.class);

    final AuditionLogger auditionLogger;
    final ObjectMapper objectMapper;

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution)
        throws IOException {

        final var method = request.getMethod();
        final var uri = request.getURI();

        // Constructing request URL, Method and Headers for logging
        final String requestInfo = String.format("""
                [Request] %s %s
                Headers : %s
                Body    : %s
                """,
            method,
            uri,
            request.getHeaders(),
            body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "<empty>"
        );

        auditionLogger.info(LOGGER, requestInfo);

        final ClientHttpResponse response = execution.execute(request, body);

        // Constructing response status code, headers for request URL, Method and Headers for logging
        final StringBuilder responseInfo = new StringBuilder(80);

        responseInfo.append("[Response] ").append(method).append(' ').append(uri)
            .append("\nStatus    :").append(response.getStatusCode())
            .append("\nHeaders   :").append(response.getHeaders());

        if (LOGGER.isDebugEnabled()) {
            final byte[] responseBody = response.getBody().readAllBytes();
            auditionLogger.debug(LOGGER, responseInfo.append("\n    Body: ")
                .append(getSafeResponseBodyAsString(responseBody)).toString());
        } else {
            auditionLogger.info(LOGGER, responseInfo.toString());
        }

        return response;
    }

    private String getSafeResponseBodyAsString(final byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0) {
            return "<empty>";
        }

        try {
            return objectMapper.readValue(responseBody, Object.class).toString();
        } catch (IOException e) {
            // Fallback to raw string
            return new String(responseBody, StandardCharsets.UTF_8);
        }
    }
}
