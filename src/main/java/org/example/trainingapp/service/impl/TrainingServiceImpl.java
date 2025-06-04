package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = Logger.getLogger(TrainingServiceImpl.class.getName());
    private final TrainingDao trainingDao;

    @Autowired
    public TrainingServiceImpl(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public void createTraining(Training training) {
        trainingDao.save(training);
        log.info("Trainee created: " + training.getTrainingName());
    }

    @Override
    @Transactional(readOnly = true)
    public Training getTraining(Long id) {
        Optional<Training> trainingOpt = trainingDao.findById(id);
        if (trainingOpt.isEmpty()) {
            log.warning("Training not found: ID=" + id);
        }
        return trainingOpt.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }
}