package org.example.trainingapp.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.filter.TransactionIdFilter;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TrainerHoursClient {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final JwtTokenUtil jwtTokenUtil;

    private static final Logger log = LoggerFactory.getLogger(TrainerHoursClient.class);

    private String serviceToken;                //  token for service calls without outer request

    @PostConstruct
    public void init() throws Exception {       //  generating system token
        UserDetails systemUser = User.withUsername("system")
                .password("N/A")
                .roles("TRAINER")
                .build();
        this.serviceToken = jwtTokenUtil.generateToken(systemUser);
        log.info("Service JWT generated for inter-service calls");
    }

    private String resolveToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String auth = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                return auth.substring(7);
            }
        }
        return serviceToken;                //  using service token when calling service for inner purposes
    }

    private String currentTxId() {          // transactionId for other microservice
        String txId = MDC.get("txId");
        return StringUtils.hasText(txId) ? txId : UUID.randomUUID().toString();
    }

    public void notifyTrainerHours(TrainingUpdateRequest request) {
        notifyTrainerHours(request, currentTxId());
    }


    private void fallbackNotify(TrainingUpdateRequest request) {
        // TODO: Fallback logic - place the request in a local queue for later resending
        log.warn("FALLBACK: Training hours update postponed for request {}", request);
    }

    public void notifyTrainerHours(TrainingUpdateRequest request, String txId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("trainerHoursService");
        circuitBreaker.run(() -> {
            HttpHeaders headers = buildHeaders(txId);                       //  forwarding transactionId in header
            HttpEntity<TrainingUpdateRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity("http://training-hours-service/api/trainer-hours/events", entity, Void.class);
            log.info("Successfully notified trainer hours microservice: {}", request);
            return null;
        }, throwable -> {
            log.error("Trainer-hours service unavailable, fallback activated: {}", throwable.getMessage());
            fallbackNotify(request);
            return null;
        });
    }

    public void clearAllTrainerHours(String txId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("trainerHoursService");
        circuitBreaker.run(() -> {
            HttpHeaders headers = buildHeaders(txId);                       //  forwarding transactionId in header
                    restTemplate.exchange("http://training-hours-service/api/trainer-hours", HttpMethod.DELETE,
                            new HttpEntity<>(headers), Void.class);
                    log.info("Successfully cleared trainer-hours microservice data.");
                    return null;
                }, throwable -> {
                    log.error("Fallback: cannot clear trainer-hours data right now, cause: {}", throwable.getMessage());
                    return null;
                }
        );
    }

    private HttpHeaders buildHeaders(String txId) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(resolveToken());
        if (StringUtils.hasText(txId)) {
            h.add(TransactionIdFilter.TX_HEADER, txId);
            MDC.put("txId", txId);
        }
        return h;
    }

    public double getTrainerHours(String username, int year, int month) {
        CircuitBreaker cb = circuitBreakerFactory.create("trainerHoursService");
        return cb.run(() -> {
            HttpHeaders headers = buildHeaders(currentTxId());
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Double> response = restTemplate.exchange(
                    "http://training-hours-service/api/trainer-hours/{username}/hours?year={year}&month={month}",
                    HttpMethod.GET,
                    entity,
                    Double.class,
                    username, year, month
            );
            return response.getBody() != null ? response.getBody() : 0.0;
        }, throwable -> {
            log.error("Fallback: cannot fetch trainer hours: {}", throwable.getMessage());
            return 0.0;
        });
    }
}

