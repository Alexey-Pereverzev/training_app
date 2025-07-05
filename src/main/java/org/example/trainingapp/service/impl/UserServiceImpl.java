package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.service.UserService;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class.getName());
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final AuthenticationService authenticationService;

    public UserServiceImpl(TraineeService traineeService, TrainerService trainerService,
                           AuthenticationService authenticationService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINER, Role.TRAINEE})
    public void changePassword(ChangePasswordDto changePasswordDto) {
        ValidationUtils.validateCredentials(changePasswordDto);
        Role role = authenticationService.authorize(changePasswordDto.getUsername(), changePasswordDto.getOldPassword());
        if (role==Role.TRAINER) {
            trainerService.setNewPassword(changePasswordDto.getUsername(), changePasswordDto.getOldPassword(),
                    changePasswordDto.getNewPassword());
        } else {
            traineeService.setNewPassword(changePasswordDto.getUsername(), changePasswordDto.getOldPassword(),
                    changePasswordDto.getNewPassword());
        }
        log.info("Password changed for user: {}", changePasswordDto.getUsername());
    }
}
