package org.example.trainingapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionIdFilter extends OncePerRequestFilter {             //  generate or pass txId
    public static final String TX_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String txId = Optional.ofNullable(req.getHeader(TX_HEADER))
                .filter(StringUtils::hasText)               // take txId from the header if it arrived
                .orElse(UUID.randomUUID().toString());      // otherwise generate
        MDC.put("txId", txId);
        res.setHeader(TX_HEADER, txId);                     // put it in the answer for the client
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}

