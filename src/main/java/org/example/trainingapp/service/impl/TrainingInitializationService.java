package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActionType;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class TrainingInitializationService {

    private final TrainingRepository trainingRepository;
    private final TrainerHoursClient trainerHoursClient;
    private final Converter converter;
    private final Logger log = LoggerFactory.getLogger(TrainingInitializationService.class);


    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void initializeTrainerHoursMicroservice() {
        final String txId = Optional.ofNullable(MDC.get("txId"))        //  generating txId for initialization of 2nd service
                .orElseGet(() -> {
                    String id = UUID.randomUUID().toString();
                    MDC.put("txId", id);
                    return id;
                });

        long started = System.currentTimeMillis();
        log.info("Initializing trainer-hours microservice...");

        try {
            try {                                                           // cleaning up old records from mongo
                trainerHoursClient.clearAllTrainerHours(txId);
                log.info("Trainer-hours microservice cleared successfully. txId={}", txId);
            } catch (Exception e) {
                log.error("Failed to clear trainer-hours microservice before init: {}", e.getMessage(), e);
                return;
            }

            List<Training> allTrainings = trainingRepository.findAll();         // loading all trainings to mongo
            int total = allTrainings.size();
            AtomicInteger success = new AtomicInteger();

            for (Training training : allTrainings) {
                try {
                    TrainingUpdateRequest update = converter.trainingAndActionToUpdateRequest(training, ActionType.ADD);
                    trainerHoursClient.notifyTrainerHours(update, txId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    log.warn("Failed to initialize training '{}': {}", training.getTrainingName(), e.getMessage(), e);
                }
            }
            long took = System.currentTimeMillis() - started;
            log.info("Trainer-hours microservice initialization complete: {}/{} trainings sent, took {} ms. txId={}",
                    success.get(), total, took, txId);
        } finally {
            MDC.remove("txId");
        }
    }
}

