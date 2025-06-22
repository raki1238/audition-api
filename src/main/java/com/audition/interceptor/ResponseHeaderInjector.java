package com.audition.interceptor;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ResponseHeaderInjector implements HandlerInterceptor {

    final Tracer tracer;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        //Added the Response headers with the Trace-Id and Span-Id
        if (tracer != null && tracer.currentSpan() != null) {
            final Span currentSpan = tracer.currentSpan();
            final Span span = currentSpan == null ? tracer.nextSpan() : currentSpan;
            response.setHeader("X-Trace-Id", span.context().traceIdString());
            response.setHeader("X-Span-Id", span.context().spanIdString());
        }
        return true;
    }
}
