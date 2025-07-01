package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void whenChangingPassword_forTrainer_shouldCallTrainerService() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("Elena.Sokolova", "oldPass", "newPass");
        when(authenticationService.authorize("Elena.Sokolova", "oldPass"))
                .thenReturn(Role.TRAINER);
        // when
        userService.changePassword("someAuthHeader", dto);
        // then
        verify(trainerService).setNewPassword("Elena.Sokolova", "oldPass", "newPass");
        verifyNoInteractions(traineeService);
    }


    @Test
    void whenChangingPassword_forTrainee_shouldCallTraineeService() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("Anna.Ivanova", "123", "456");
        when(authenticationService.authorize("Anna.Ivanova", "123"))
                .thenReturn(Role.TRAINEE);
        // when
        userService.changePassword("someAuthHeader", dto);
        // then
        verify(traineeService).setNewPassword("Anna.Ivanova", "123", "456");
        verifyNoInteractions(trainerService);
    }


    @Test
    void whenChangingPassword_withInvalidCredentials_shouldThrow() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("", "", "");
        // when + then
        assertThatThrownBy(() -> userService.changePassword("auth", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
        verifyNoInteractions(trainerService, traineeService, authenticationService);
    }
}
