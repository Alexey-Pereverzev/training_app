package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.config.JmsConfig;
import org.example.trainingapp.dto.EventType;
import org.example.trainingapp.dto.TrainerHoursEvent;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.exception.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TrainerHoursPublisher {

    private final JmsTemplate jmsTemplate;
    private final CircuitBreakerFactory<?, ?> cbFactory;
    private static final Logger log = LoggerFactory.getLogger(TrainerHoursPublisher.class);


    private String newTxId() {                                  // new transactionId
        return UUID.randomUUID().toString();
    }


    public void publishUpdate(TrainingUpdateRequest update) {
        publishUpdate(update, newTxId());                       // call with generated txId
    }


    public void publishUpdate(TrainingUpdateRequest update, String txId) {
        String previousTxId = MDC.get("txId");
        MDC.put("txId", txId);
        try {
            TrainerHoursEvent event = TrainerHoursEvent.builder()
                    .txId(txId)                                 //  forwarding transactionId in dto
                    .type(EventType.UPDATE)
                    .trainingUpdate(update)
                    .build();
            CircuitBreaker cb = cbFactory.create("trainerHoursService");
            cb.run(() -> {
                try {
                    jmsTemplate.convertAndSend(JmsConfig.TRAINING_QUEUE, event);
                    log.info("Published UPDATE, txId={}, trainer={}, action={}", txId, update.getTrainerUsername(),
                            update.getActionType());
                } catch (JmsException e) {
                    log.error("Failed to publish UPDATE, txId={}", txId, e);
                    throw new MessagingException("Publish failed for UPDATE, txId=" + txId, e);
                }
                return null;
            }, throwable -> {
                log.error("TrainerHours publish failed: {}", throwable.getMessage());
                throw new MessagingException("Publish failed (circuit breaker fallback), txId=" + txId, throwable);
            });
        } finally {
            if (previousTxId != null) {
                MDC.put("txId", previousTxId);              // recover previous txId to not destroy txId of other transaction
            } else {
                MDC.remove("txId");                         // if it is our txId - clear it from MDC
            }
        }
    }


    // Clearing all data in the second service
    public void publishClearAll(String txId) {
        String previousTxId = MDC.get("txId");
        MDC.put("txId", txId);
        try {
            TrainerHoursEvent event = TrainerHoursEvent.builder()
                    .txId(txId)                                 //  forwarding transactionId in dto
                    .type(EventType.CLEAR_ALL)
                    .build();
            CircuitBreaker cb = cbFactory.create("trainerHoursService");
            cb.run(() -> {
                try {
                    jmsTemplate.convertAndSend(JmsConfig.TRAINING_QUEUE, event);
                    log.info("Published CLEAR_ALL event, txId={}", txId);
                } catch (JmsException e) {
                    log.error("Failed to publish CLEAR_ALL, txId={}", txId, e);
                    throw new MessagingException("Publish failed for CLEAR_ALL, txId=" + txId, e);
                }
                return null;
            }, throwable -> {
                log.error("TrainerHours CLEAR_ALL publish failed: {}", throwable.getMessage(), throwable);
                throw new MessagingException("Publish failed (circuit breaker fallback), txId=" + txId, throwable);
            });
        } finally {
            if (previousTxId != null) {
                MDC.put("txId", previousTxId);
            } else {
                MDC.remove("txId");
            }
        }
    }

}

