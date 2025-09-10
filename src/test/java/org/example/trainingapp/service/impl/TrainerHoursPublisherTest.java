package org.example.trainingapp.service.impl;

import org.example.trainingapp.config.JmsConfig;
import org.example.trainingapp.dto.TrainerHoursEvent;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.exception.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerHoursPublisherTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private CircuitBreakerFactory<?, ?> cbFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private TrainerHoursPublisher publisher;

    private final String txId = "test-tx-id";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        when(cbFactory.create("trainerHoursService")).thenReturn(circuitBreaker);
        lenient().when(circuitBreaker.run(any(Supplier.class), any()))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0); // Supplier<Object>
                    return supplier.get();
                });
    }


    @Test
    void whenPublishUpdate_shouldSendEvent() {
        // given
        TrainingUpdateRequest request = new TrainingUpdateRequest();
        request.setTrainerUsername("trainer1");
        // when
        publisher.publishUpdate(request, txId);
        // then
        verify(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINING_QUEUE), any(TrainerHoursEvent.class));
    }


    @Test
    void whenPublishClearAll_shouldSendEvent() {
        // when
        publisher.publishClearAll(txId);
        // then
        verify(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINING_QUEUE), any(TrainerHoursEvent.class));
    }


    @Test
    void whenPublishUpdate_jmsException_shouldThrowMessagingException() {
        // given
        TrainingUpdateRequest request = new TrainingUpdateRequest();
        request.setTrainerUsername("trainer1");
        doThrow(new JmsException("fail") {}).when(jmsTemplate)
                .convertAndSend(eq(JmsConfig.TRAINING_QUEUE), any(TrainerHoursEvent.class));
        // when + then
        assertThatThrownBy(() -> publisher.publishUpdate(request, txId))
                .isInstanceOf(MessagingException.class)
                .hasMessageContaining("Publish failed for UPDATE");
    }


    @Test
    @SuppressWarnings("unchecked")
    void whenPublishUpdate_circuitBreakerFallback_shouldThrowMessagingException() {
        // given
        TrainingUpdateRequest request = new TrainingUpdateRequest();
        request.setTrainerUsername("trainer1");
        when(circuitBreaker.run(any(Supplier.class), any())).thenAnswer(invocation -> {
            Function<Throwable, ?> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("cb fail"));
        });
        // when + then
        assertThatThrownBy(() -> publisher.publishUpdate(request, txId))
                .isInstanceOf(MessagingException.class)
                .hasMessageContaining("circuit breaker fallback");
    }


    @Test
    void whenPublishUpdate_shouldRestorePreviousMdcTxId() {
        // given
        MDC.put("txId", "old-tx-id");
        TrainingUpdateRequest request = new TrainingUpdateRequest();
        request.setTrainerUsername("trainer1");
        // when
        publisher.publishUpdate(request, txId);
        // then
        assertThat(MDC.get("txId")).isEqualTo("old-tx-id");
        MDC.clear();
    }
}
