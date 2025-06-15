package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.service.TrainingService;
import org.example.trainingapp.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = Logger.getLogger(TrainingServiceImpl.class.getName());
    private final TrainingDao trainingDao;
    private final Converter converter;

    @Autowired
    public TrainingServiceImpl(TrainingDao trainingDao, Converter converter) {
        this.trainingDao = trainingDao;
        this.converter = converter;
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER}, checkOwnership = false)
    public void createTraining(String username, String password, TrainingDto trainingDto) {
        ValidationUtils.validateTraining(trainingDto);
        Training training = converter.dtoToEntity(trainingDto);
        trainingDao.save(training);
        log.info("Training created: " + training.getTrainingName());
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER, Role.TRAINEE}, checkOwnership = false)
    public TrainingDto getTraining(String username, String password, Long id) {
        Training training = getTraining(id);
        log.info("Retrieved training: ID=" + id);
        return converter.entityToDto(training);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER, Role.TRAINEE}, checkOwnership = false)
    public List<TrainingDto> getAllTrainings(String username, String password) {
        log.info("Retrieved all trainings requested by: " + username);
        return trainingDao.findAll().stream().map(converter::entityToDto).toList();
    }


    private Training getTraining(Long id) {
        return trainingDao.findById(id).orElseThrow(() -> {
            log.warning("Training not found: ID=" + id);
            return new RuntimeException("Training not found with ID: " + id);
        });
    }

}