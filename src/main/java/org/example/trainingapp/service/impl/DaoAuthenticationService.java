package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;


@Service
public class DaoAuthenticationService implements AuthenticationService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private static final Logger logger = Logger.getLogger(DaoAuthenticationService.class.getName());

    @Autowired
    public DaoAuthenticationService(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @Override
    public Role validateCredentials(String username, String password) {
        Optional<Trainer> trainerOpt = trainerDao.findByUsername(username);
        if (trainerOpt.isPresent() && trainerOpt.get().getPassword().equals(password)) {
            logger.info("Trainer authentication successful: " + username);
            return Role.TRAINER;
        } else {
            logger.warning("Trainer authentication failed (wrong password): " + username);
        }
        Optional<Trainee> traineeOpt = traineeDao.findByUsername(username);
        if (traineeOpt.isPresent() && traineeOpt.get().getPassword().equals(password)) {
            logger.info("Trainee authentication successful: " + username);
            return Role.TRAINEE;
        } else {
            logger.warning("Trainee authentication failed (wrong password): " + username);
        }

        logger.severe("Authentication failed: No user found with username '" + username + "'");
        throw new SecurityException("Invalid credentials");
    }
}
