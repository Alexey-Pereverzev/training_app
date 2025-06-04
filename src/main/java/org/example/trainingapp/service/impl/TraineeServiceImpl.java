package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.util.CredentialsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = Logger.getLogger(TraineeServiceImpl.class.getName());
    private final TraineeDao traineeDao;

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void createTrainee(Trainee trainee) {
        List<String> existingUsernames = traineeDao.findAll().stream()
                .map(Trainee::getUsername)
                .toList();

        String generatedUsername = CredentialsUtil.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        trainee.setUsername(generatedUsername);
        trainee.setPassword(password);
        trainee.setActive(true);

        traineeDao.save(trainee);
        log.info("Trainee created: " + trainee.getUsername());
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
    @Transactional(readOnly = true)
    public Trainee getTrainee(Long id) {
        Optional<Trainee> traineeOpt = traineeDao.findById(id);
        if (traineeOpt.isEmpty()) {
            log.warning("Trainee not found: ID=" + id);
        }
        return traineeOpt.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }
}
