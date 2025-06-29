package org.example.trainingapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingInterceptor.class.getName());

    @Override
    public boolean preHandle(HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull Object handler) {
        log.info("REST-IN  {} {}", req.getMethod(), req.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res,
                                @NonNull Object handler, Exception ex) {
        if (ex == null) {
            log.info("REST-OUT {} {} -> {}", req.getMethod(), req.getRequestURI(), res.getStatus());
        } else {
            log.warn("REST-ERR {} {} -> {} msg={}", req.getMethod(), req.getRequestURI(), res.getStatus(), ex.getMessage());
        }
    }
}

