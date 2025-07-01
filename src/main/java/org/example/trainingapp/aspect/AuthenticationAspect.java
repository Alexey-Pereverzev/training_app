package org.example.trainingapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
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
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.util.AuthUtil;
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
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationAspect.class.getName());

    @Autowired
    public AuthenticationAspect(AuthenticationService authenticationService, TraineeDao traineeDao, TrainerDao trainerDao) {
        this.authenticationService = authenticationService;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }


    @Before("@annotation(requiresAuth)")
    public void authenticate(JoinPoint joinPoint, RequiresAuthentication requiresAuth) {
        Object[] args = joinPoint.getArgs();

        if (args.length < 1 || !(args[0] instanceof String authHeader)) {
            logger.error("Authentication failed: method did not receive authorization header as first argument");
            throw new SecurityException("Auth header must be the first argument.");
        }

        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);       //  decode and check format
        Role authenticatedRole = authenticationService.authorize(credentialsDto);   //  validate
        String username = credentialsDto.getUsername();
        logger.info("User '{}' authenticated as role: {}", username, authenticatedRole);

        boolean isAuthorized = Arrays.stream(requiresAuth.allowedRoles()).anyMatch(role -> role == authenticatedRole);

        if (!isAuthorized) {                                                        //  matching roles
            String message = "Access denied: role '" + authenticatedRole + "' is not allowed for this operation.";
            logger.warn(message);
            throw new ForbiddenAccessException(message);
        }

        if (requiresAuth.checkOwnership()) {            //  checking if the entity which owns method is passed in args:
            if (authenticatedRole  == Role.TRAINEE) {   //  if related entity is different from authenticated user
                                                        //  then throw an exception
                for (Object arg : args) {
                    Optional<Trainee> dbTrainee = Optional.empty();
                    if (arg instanceof TraineeRequestDto traineeRequestDto) {               //  if passed in TraineeRequestDto
                        dbTrainee = traineeDao.findByUsername(traineeRequestDto.getUsername());
                    } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                        dbTrainee = traineeDao.findByUsername(activeStatusDto.getUsername());
                    } else if (arg instanceof UpdateTrainerListDto updateTrainerListDto) {  //  if passed in UpdateTrainerListDto
                        dbTrainee = traineeDao.findByUsername(updateTrainerListDto.getUsername());
                    } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                        dbTrainee = traineeDao.findByUsername(changePasswordDto.getUsername());
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
                        dbTrainer = trainerDao.findByUsername(trainerRequestDto.getUsername());
                    } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                        dbTrainer = trainerDao.findByUsername(activeStatusDto.getUsername());
                    } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                        dbTrainer = trainerDao.findByUsername(changePasswordDto.getUsername());
                    } else if (arg instanceof TrainingRequestDto trainingRequestDto) {      //  if passed in TrainingRequestDto
                        dbTrainer = trainerDao.findByUsername(trainingRequestDto.getTrainerName());
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
