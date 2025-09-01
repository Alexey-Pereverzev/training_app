package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActionType;
import org.example.trainingapp.dto.SyncResult;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class TrainingSyncService {

    private final TrainingRepository trainingRepository;
    private final TrainerHoursPublisher trainerHoursPublisher;
    private final Converter converter;
    private final Logger log = LoggerFactory.getLogger(TrainingSyncService.class);


    @Transactional(readOnly = true)
    public SyncResult syncTrainerHours() {
        final String txId = Optional.ofNullable(MDC.get("txId"))        //  generating txId for initialization of 2nd service
                .orElseGet(() -> {
                    String id = UUID.randomUUID().toString();
                    MDC.put("txId", id);
                    return id;
                });
        log.info("Starting on-demand trainer-hours sync, txId={}", txId);

        try {
            long started = System.currentTimeMillis();
            trainerHoursPublisher.publishClearAll(txId);                        // cleaning up old records
            log.info("Trainer-hours microservice cleared successfully. txId={}", txId);
            List<Training> allTrainings = trainingRepository.findAll();         // loading all trainings to mongo
            int total = allTrainings.size();                                    //  trainings to sync counter
            AtomicInteger success = new AtomicInteger();                        //  successful updates

            for (Training training : allTrainings) {
                try {
                    TrainingUpdateRequest update = converter.trainingAndActionToUpdateRequest(training, ActionType.ADD);
                    trainerHoursPublisher.publishUpdate(update, txId);
                    success.incrementAndGet();
                } catch (Exception e) {
                    log.warn("Failed to initialize training '{}': {}", training.getTrainingName(), e.getMessage(), e);
                }
            }
            long took = System.currentTimeMillis() - started;
            int successfulCount = success.get();
            log.info("Trainer-hours microservice initialization complete: {}/{} trainings sent, took {} ms. txId={}",
                    successfulCount, total, took, txId);
            return new SyncResult(txId, total, successfulCount, took);
        } catch (Exception e) {
            log.error("Initialization failed: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("txId");
        }
    }
}

