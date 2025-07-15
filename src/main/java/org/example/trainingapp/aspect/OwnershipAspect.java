package org.example.trainingapp.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {


    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private static final Logger log = LoggerFactory.getLogger(OwnershipAspect.class.getName());


    @Before("@annotation(checkOwnership)")
    public void authenticate(JoinPoint joinPoint, CheckOwnership checkOwnership) {
        if (!checkOwnership.value()) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }

        String username = auth.getName();
        String roleName = auth.getAuthorities().iterator().next().getAuthority(); // e.g. ROLE_TRAINER
        Role role = Role.valueOf(roleName.replace("ROLE_", ""));

        log.info("Checking ownership for user '{}' with role '{}'", username, role);

        Object[] args = joinPoint.getArgs();        //  checking if the entity which owns method is passed in args:
        if (role  == Role.TRAINEE) {                //  if related entity is different from authenticated user
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
                    log.warn(message);
                    throw new ForbiddenAccessException(message);
                } else if (dbTrainee.isPresent()) {
                    return;                 // ownership confirmed
                }
            }
        }

        if (role == Role.TRAINER) {
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
                    log.warn(message);
                    throw new ForbiddenAccessException(message);
                } else if (dbTrainer.isPresent()) {
                    return;                 // ownership confirmed
                }
            }
        }
    }

}
