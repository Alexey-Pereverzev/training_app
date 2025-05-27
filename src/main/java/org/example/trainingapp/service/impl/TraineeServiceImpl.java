package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.util.CredentialsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = Logger.getLogger(TraineeServiceImpl.class.getName());
    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void createTrainee(Trainee trainee) {
        List<String> existingUsernames = traineeDao.findAll().stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        String generatedUsername = CredentialsUtil.generateUsername(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        User user = trainee.getUser();
        user.setUsername(generatedUsername);
        user.setPassword(password);
        user.setActive(true);

        traineeDao.save(trainee);
        log.info("Trainee created: " + user.getUsername());
    }


    @Override
    public void updateTrainee(Trainee trainee) {
        traineeDao.update(trainee);
        log.info("Trainee updated: " + trainee.getId());
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeDao.deleteById(id);
        log.info("Trainee deleted: " + id);
    }

    @Override
    public Trainee getTrainee(Long id) {
        Optional<Trainee> traineeOpt = traineeDao.findById(id);
        if (traineeOpt.isEmpty()) {
            log.warning("Trainee not found: ID=" + id);
        }
        return traineeOpt.orElse(null);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }
}
