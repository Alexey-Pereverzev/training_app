package org.example.trainingapp.service.impl;

import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;


    @Test
    void whenLoadUserByUsername_trainerExists_shouldReturnUserDetailsWithTrainerRole() {
        // given
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setPassword("pass");
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        // when
        UserDetails details = userDetailsService.loadUserByUsername("trainer1");
        // then
        assertThat(details.getUsername()).isEqualTo("trainer1");
        assertThat(details.getPassword()).isEqualTo("pass");
        assertThat(details.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
    }


    @Test
    void whenLoadUserByUsername_traineeExists_shouldReturnUserDetailsWithTraineeRole() {
        // given
        when(trainerRepository.findByUsername("trainee1")).thenReturn(Optional.empty());
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        trainee.setPassword("pass");
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        // when
        UserDetails details = userDetailsService.loadUserByUsername("trainee1");
        // then
        assertThat(details.getUsername()).isEqualTo("trainee1");
        assertThat(details.getPassword()).isEqualTo("pass");
        assertThat(details.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE"));
    }

    @Test
    void whenLoadUserByUsername_userNotFound_shouldThrowException() {
        // given
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        // when + then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}

