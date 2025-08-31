package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.config.JmsConfig;
import org.example.trainingapp.dto.EventType;
import org.example.trainingapp.dto.TrainerHoursEvent;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TrainerHoursPublisher {

    private final JmsTemplate jmsTemplate;
    private final CircuitBreakerFactory<?, ?> cbFactory;
    private static final Logger log = LoggerFactory.getLogger(TrainerHoursPublisher.class);


    private String currentTxId() {                          // transactionId for other microservice
        String txId = MDC.get("txId");
        if (!StringUtils.hasText(txId)) {
            txId = UUID.randomUUID().toString();
            MDC.put("txId", txId);
        }
        return txId;
    }


    public void publishUpdate(TrainingUpdateRequest update) {
        publishUpdate(update, currentTxId());               // call with generated txId
    }


    public void publishUpdate(TrainingUpdateRequest update, String txId) {
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId(txId)                                 //  forwarding transactionId in dto
                .type(EventType.UPDATE)
                .trainingUpdate(update)
                .build();
        CircuitBreaker cb = cbFactory.create("trainerHoursService");
        cb.run(() -> {
            jmsTemplate.convertAndSend(JmsConfig.TRAINING_QUEUE, event);
            log.info("Published UPDATE, txId={}, trainer={}, action={}", txId, update.getTrainerUsername(),
                    update.getActionType());
            return null;
        }, throwable -> {
            log.error("TrainerHours publish failed: {}", throwable.getMessage());
            return null;
        });
    }


    // Clearing all data in the second service
    public void publishClearAll(String txId) {
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId(txId)                                 //  forwarding transactionId in dto
                .type(EventType.CLEAR_ALL)
                .build();
        CircuitBreaker cb = cbFactory.create("trainerHoursService");
        cb.run(() -> {
            jmsTemplate.convertAndSend(JmsConfig.TRAINING_QUEUE, event);
            log.info("Published CLEAR_ALL event, txId={}", txId);
            return null;
        }, throwable -> {
            log.error("TrainerHours CLEAR_ALL publish failed: {}", throwable.getMessage(), throwable);
            return null;
        });
    }

}


