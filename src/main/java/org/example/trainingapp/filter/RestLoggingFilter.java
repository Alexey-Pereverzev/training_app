package org.example.trainingapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)                                      // after TransactionIdFilter
public class RestLoggingFilter extends OncePerRequestFilter {               // log input/output of REST requests

    private static final Logger log = LoggerFactory.getLogger(RestLoggingFilter.class);
    private static final int MAX_LOG_BYTES = 4096; // ограничим размер
    private static final Set<String> SENSITIVE_ENDPOINTS = Set.of(          //  do not log body for sensitive endpoints
            "/api/users/login",
            "/api/users/register-trainee",
            "/api/users/register-trainer",
            "/api/users/change-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(res);

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long tookMs = System.currentTimeMillis() - start;
            String uri = req.getRequestURI();
            String method = req.getMethod();
            String query = StringUtils.hasText(req.getQueryString()) ? "?" + req.getQueryString() : "";

            boolean sensitive = isSensitive(method, uri);
            String reqBody = sensitive ? "<hidden>" :
                    safeBody(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            String resBody = sensitive ? "<hidden>" :
                    safeBody(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

            if (responseWrapper.getStatus() < 400) {
                log.info("REST {} {}{} -> {} ({} ms)\nREQ:{}\nRES:{}",
                        method, uri, query, responseWrapper.getStatus(), tookMs, reqBody, resBody);
            } else {
                log.warn("REST {} {}{} -> {} ({} ms)\nREQ:{}\nRES:{}",
                        method, uri, query, responseWrapper.getStatus(), tookMs, reqBody, resBody);
            }
            responseWrapper.copyBodyToResponse();                           //  return body to client
        }
    }

    private boolean isSensitive(String method, String uri) {
        return "POST".equalsIgnoreCase(method) && SENSITIVE_ENDPOINTS.stream().anyMatch(uri::startsWith);
    }

    private String safeBody(byte[] buf, String enc) {
        if (buf == null || buf.length == 0) return "<empty>";
        int len = Math.min(buf.length, MAX_LOG_BYTES);
        try {
            return new String(buf, 0, len, enc);
        } catch (Exception e) {
            return "<binary:" + len + " bytes>";
        }
    }
}

