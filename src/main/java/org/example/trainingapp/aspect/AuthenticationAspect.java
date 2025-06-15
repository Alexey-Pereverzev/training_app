package org.example.trainingapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dto.TraineeDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;


@Aspect
@Component
public class AuthenticationAspect {

    private final AuthenticationService authenticationService;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private static final Logger logger = Logger.getLogger(AuthenticationAspect.class.getName());

    @Autowired
    public AuthenticationAspect(AuthenticationService authenticationService, TraineeDao traineeDao, TrainerDao trainerDao) {
        this.authenticationService = authenticationService;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }


    @Before("@annotation(requiresAuth)")
    public void authenticate(JoinPoint joinPoint, RequiresAuthentication requiresAuth) {
        Object[] args = joinPoint.getArgs();

        if (args.length < 2 || !(args[0] instanceof String username) || !(args[1] instanceof String password)) {
            logger.severe("Authentication failed: method did not receive username and password as first two args");
            throw new SecurityException("Username and password must be the first two arguments.");
        }

        Role authenticatedRole = authenticationService.validateCredentials(username, password);
        logger.info("User '" + username + "' authenticated as role: " + authenticatedRole);

        boolean isAuthorized = Arrays.stream(requiresAuth.allowedRoles()).anyMatch(role -> role == authenticatedRole);

        if (!isAuthorized) {
            String message = "Access denied: role '" + authenticatedRole + "' is not allowed for this operation.";
            logger.warning(message);
            throw new SecurityException(message);
        }

        if (requiresAuth.checkOwnership()) {
            if (authenticatedRole  == Role.TRAINEE) {   //  checking if the entity which owns method is passed in args
                                                        //  if related entity is different from authenticated user
                                                        //  then throw an exception
                for (Object arg : args) {
                    Long id = null;
                    if (arg instanceof TraineeDto traineeDto) {          //  if passed as entity
                        id = traineeDto.getId();
                    } else if (arg instanceof Long) {                    //  if passed via id
                        id = (Long) arg;
                    }
                    if (id != null) {
                        Optional<Trainee> dbTrainee = traineeDao.findById(id);
                        if (dbTrainee.isPresent() && !dbTrainee.get().getUsername().equals(username)) {
                            String message = "Access denied for trainee: " + username + " (ID " + id
                                    + " does not match).";
                            logger.warning(message);
                            throw new SecurityException(message);
                        }
                    }
                }
            }

            if (authenticatedRole  == Role.TRAINER) {
                for (Object arg : args) {
                    Long id = null;
                    if (arg instanceof TrainerDto trainerDto) {          //  if passed as entity
                        id = trainerDto.getId();
                    } else if (arg instanceof Long) {                    //  if passed via id
                        id = (Long) arg;
                    }
                    if (id != null) {
                        Optional<Trainer> dbTrainer = trainerDao.findById(id);
                        if (dbTrainer.isPresent() && !dbTrainer.get().getUsername().equals(username)) {
                            String message = "Access denied for trainer: " + username + " (ID " + id
                                    + " does not match).";
                            logger.warning(message);
                            throw new SecurityException(message);
                        }
                    }
                }
            }
        }
    }

}
