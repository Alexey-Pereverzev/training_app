package org.example.trainingapp.service.impl;

import org.example.trainingapp.dto.ActionType;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.filter.TransactionIdFilter;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerHoursClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    @Mock
    private CircuitBreaker circuitBreaker;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private TrainerHoursClient client;

    private TrainingUpdateRequest req;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        // given
        when(jwtTokenUtil.generateToken(any())).thenReturn("serviceToken");
        when(circuitBreakerFactory.create(any())).thenReturn(circuitBreaker);
        lenient().when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
                .thenAnswer(inv -> ((Supplier<Object>) inv.getArgument(0)).get());
        client.init();
        req = TrainingUpdateRequest.builder()
                .trainerUsername("Ivan.Ivanov")
                .trainerFirstName("Ivan")
                .trainerLastName("Ivanov")
                .trainingDate(LocalDate.of(2025, 1, 1))
                .trainingDuration(60)
                .active(true)
                .actionType(ActionType.ADD)
                .build();
    }


    @SuppressWarnings("unchecked")
    @Test
    void whenNotifyTrainerHours_success_shouldCallRestTemplate() {
        // when
        client.notifyTrainerHours(req, "tx123");
        // then
        ArgumentCaptor<HttpEntity<TrainingUpdateRequest>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(
                eq("http://training-hours-service/api/trainer-hours/events"),
                captor.capture(),
                eq(Void.class)
        );
        HttpHeaders headers = captor.getValue().getHeaders();
        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer serviceToken");
        assertThat(headers.getFirst(TransactionIdFilter.TX_HEADER)).isEqualTo("tx123");
        assertThat(captor.getValue().getBody()).isEqualTo(req);
    }


    @SuppressWarnings("unchecked")
    @Test
    void whenNotifyTrainerHours_fails_shouldFallback() {
        // given
        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
                .thenAnswer(inv -> ((Function<Throwable, ?>) inv.getArgument(1)).apply(new RuntimeException("down")));
        // when
        client.notifyTrainerHours(req, "tx123");
        // then
        verifyNoInteractions(restTemplate);
    }


    @SuppressWarnings("unchecked")
    @Test
    void whenClearAllTrainerHours_success_shouldCallRestTemplate() {
        // when
        client.clearAllTrainerHours("tx999");
        // then
        ArgumentCaptor<HttpEntity<?>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://training-hours-service/api/trainer-hours"),
                eq(HttpMethod.DELETE),
                captor.capture(),
                eq(Void.class)
        );
        HttpHeaders headers = captor.getValue().getHeaders();
        assertThat(headers.getFirst(TransactionIdFilter.TX_HEADER)).isEqualTo("tx999");
    }


    @Test
    void whenGetTrainerHours_success_shouldReturnValue() {
        // given
        ResponseEntity<Double> resp = ResponseEntity.ok(12.5);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Double.class),
                any(), any(), any()
        )).thenReturn(resp);
        // when
        double result = client.getTrainerHours("Ivan.Ivanov", 2025, 1);
        // then
        assertThat(result).isEqualTo(12.5);
    }


    @Test
    void whenGetTrainerHours_nullBody_shouldReturnZero() {
        // given
        ResponseEntity<Double> resp = ResponseEntity.ok(null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Double.class),
                any(), any(), any())).thenReturn(resp);
        // when
        double result = client.getTrainerHours("Ivan.Ivanov", 2025, 1);
        // then
        assertThat(result).isZero();
    }


    @SuppressWarnings("unchecked")
    @Test
    void whenGetTrainerHours_fails_shouldFallbackToZero() {
        // given
        when(circuitBreaker.run(any(Supplier.class), any(Function.class)))
                .thenAnswer(inv -> ((Function<Throwable, ?>) inv.getArgument(1)).apply(new RuntimeException("fail")));
        // when
        double result = client.getTrainerHours("Ivan.Ivanov", 2025, 1);
        // then
        assertThat(result).isZero();
        verifyNoInteractions(restTemplate);
    }
}

