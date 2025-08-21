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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
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
            log.warn("User is not authenticated");
            throw new SecurityException("User is not authenticated");
        }

        String current = auth.getName();
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
        if (auths == null || auths.isEmpty()) {
            log.warn("User has no authorities");
            throw new SecurityException("User has no authorities");
        }
        String roleName = auth.getAuthorities().iterator().next().getAuthority();
        Role role = Role.valueOf(roleName.replace("ROLE_", ""));

        log.info("Checking ownership for user '{}' with role '{}'", current, role);
        Object[] args = joinPoint.getArgs();        //  checking if the entity which owns method is passed in args:

        String dtoUsername;

        if (role  == Role.TRAINEE) {                //  if related entity is different from authenticated user
                                                        //  then throw an exception
            for (Object arg : args) {
                dtoUsername = null;
                if (arg instanceof TraineeRequestDto traineeRequestDto) {               //  if passed in TraineeRequestDto
                    dtoUsername = traineeRequestDto.getUsername();
                } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                    dtoUsername = activeStatusDto.getUsername();
                } else if (arg instanceof UpdateTrainerListDto updateTrainerListDto) {  //  if passed in UpdateTrainerListDto
                    dtoUsername = updateTrainerListDto.getUsername();
                } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                    dtoUsername = changePasswordDto.getUsername();
                }
                if (!StringUtils.hasText(dtoUsername)) continue;

                Optional<Trainee> dbTrainee = traineeRepository.findByUsername(dtoUsername);
                if (dbTrainee.isEmpty()) {
                    log.warn("Trainee not found: {}", dtoUsername);
                    throw new UsernameNotFoundException("Trainee not found: " + dtoUsername);
                }
                if (!dbTrainee.get().getUsername().equals(current)) {
                    String message = "Access denied: user " + current + " tried to access trainee "
                            + dbTrainee.get().getUsername();
                    log.warn(message);
                    throw new ForbiddenAccessException(message);
                }
                return;                         // ownership confirmed
            }
        } else if (role == Role.TRAINER) {
            for (Object arg : args) {
                dtoUsername = null;
                if (arg instanceof TrainerRequestDto trainerRequestDto) {               //  if passed in TrainerRequestDto
                    dtoUsername = trainerRequestDto.getUsername();
                } else if (arg instanceof ActiveStatusDto activeStatusDto) {            //  if passed in ActiveStatusDto
                    dtoUsername = activeStatusDto.getUsername();
                } else if (arg instanceof ChangePasswordDto changePasswordDto) {        //  if passed in ChangePasswordDto
                    dtoUsername = changePasswordDto.getUsername();
                } else if (arg instanceof TrainingRequestDto trainingRequestDto) {      //  if passed in TrainingRequestDto
                    dtoUsername = trainingRequestDto.getTrainerName();
                }
                if (!StringUtils.hasText(dtoUsername)) continue;

                Optional<Trainer> dbTrainer = trainerRepository.findByUsername(dtoUsername);
                if (dbTrainer.isEmpty()) {
                    log.warn("Trainer not found: {}", dtoUsername);
                    throw new UsernameNotFoundException("Trainer not found: " + dtoUsername);
                }
                if (!dbTrainer.get().getUsername().equals(current)) {
                    String message = "Access denied: user " + current + " tried to access trainer "
                            + dbTrainer.get().getUsername();
                    log.warn(message);
                    throw new ForbiddenAccessException(message);
                }
                return;                         // ownership confirmed
            }
        }

        if (args.length > 0 && args[0] instanceof String usernameArg) {                 // if 1st arg is String username
            if (role == Role.TRAINER && !usernameArg.equals(current)) {
                throw new ForbiddenAccessException("Access denied for trainer: " + current);
            }
            if (role == Role.TRAINEE && !usernameArg.equals(current)) {
                throw new ForbiddenAccessException("Access denied for trainee: " + current);
            }
            if (role == Role.TRAINER) {
                if (!trainerRepository.existsByUsername(usernameArg)) {
                    throw new UsernameNotFoundException("Trainer not found: " + usernameArg);
                }
            } else if (role == Role.TRAINEE) {
                if (!traineeRepository.existsByUsername(usernameArg)) {
                    throw new UsernameNotFoundException("Trainee not found: " + usernameArg);
                }
            }
            return;                                 // ownership confirmed
        }

        log.warn("Ownership could not be verified: user='{}', role='{}', method='{}', args={}", current, role,
                joinPoint.getSignature().toShortString(), java.util.Arrays.toString(args));
        throw new ForbiddenAccessException("Ownership could not be verified for user: " + current);
    }

}
