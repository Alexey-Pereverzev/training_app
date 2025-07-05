package org.example.trainingapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.util.AuthContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;


@Aspect
@Component
public class AuthenticationAspect {

    private final AuthenticationService authenticationService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final AuthContextUtil authContextUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationAspect.class.getName());

    @Autowired
    public AuthenticationAspect(AuthenticationService authenticationService, TraineeRepository traineeRepository,
                                TrainerRepository trainerRepository, AuthContextUtil authContextUtil) {
        this.authenticationService = authenticationService;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.authContextUtil = authContextUtil;
    }


    @Before("@annotation(requiresAuth)")
    public void authenticate(JoinPoint joinPoint, RequiresAuthentication requiresAuth) {
        String username;
        Role authenticatedRole;
        try {
            CredentialsDto credentials = authContextUtil.getCredentials();      // get authHeader + decode
            authenticatedRole = authenticationService.authorize(credentials);   // authorize
            username = credentials.getUsername();                               // current username
        } catch (Exception ex) {
            logger.error("Authentication failed: {}", ex.getMessage());
            throw new SecurityException("Authorization failed: " + ex.getMessage(), ex);
        }

        logger.info("User '{}' authenticated as role: {}", username, authenticatedRole);

        boolean isAuthorized = Arrays.stream(requiresAuth.allowedRoles())
                .anyMatch(role -> role == authenticatedRole);

        if (!isAuthorized) {
            String message = "Access denied: role '" + authenticatedRole + "' is not allowed for this operation.";
            logger.warn(message);
            throw new ForbiddenAccessException(message);
        }

        if (requiresAuth.checkOwnership()) {
            Object[] args = joinPoint.getArgs();        //  checking if the entity which owns method is passed in args:
            if (authenticatedRole  == Role.TRAINEE) {   //  if related entity is different from authenticated user
                                                        //  then throw an exception
                for (Object arg : args) {
                    Optional<Trainee> dbTrainee = Optional.empty();
                    if (arg instanceof TraineeRequestDto traineeRequestDto) {               //  if passed in TraineeRequestDto
                        dbTrainee = traineeRepository.findByUsername(traineeRequestDto.getUsername());
                    } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                        dbTrainee = traineeRepository.findByUsername(activeStatusDto.getUsername());
                    } else if (arg instanceof UpdateTrainerListDto updateTrainerListDto) {  //  if passed in UpdateTrainerListDto
                        dbTrainee = traineeRepository.findByUsername(updateTrainerListDto.getUsername());
                    } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                        dbTrainee = traineeRepository.findByUsername(changePasswordDto.getUsername());
                    }
                    if (dbTrainee.isPresent() && !dbTrainee.get().getUsername().equals(username)) {
                        String message = "Access denied for trainee: " + username;
                        logger.warn(message);
                        throw new ForbiddenAccessException(message);
                    }
                }
            }

            if (authenticatedRole == Role.TRAINER) {
                for (Object arg : args) {
                    Optional<Trainer> dbTrainer = Optional.empty();
                    if (arg instanceof TrainerRequestDto trainerRequestDto) {               //  if passed in TrainerRequestDto
                        dbTrainer = trainerRepository.findByUsername(trainerRequestDto.getUsername());
                    } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                        dbTrainer = trainerRepository.findByUsername(activeStatusDto.getUsername());
                    } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                        dbTrainer = trainerRepository.findByUsername(changePasswordDto.getUsername());
                    } else if (arg instanceof TrainingRequestDto trainingRequestDto) {      //  if passed in TrainingRequestDto
                        dbTrainer = trainerRepository.findByUsername(trainingRequestDto.getTrainerName());
                    }
                    if (dbTrainer.isPresent() && !dbTrainer.get().getUsername().equals(username)) {
                        String message = "Access denied for trainer: " + username;
                        logger.warn(message);
                        throw new ForbiddenAccessException(message);
                    }
                }
            }
        }
    }

}
