package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public TrainingType getOrCreate(String name) {
        return trainingTypeDao.findByName(name)
                .orElseGet(() -> {
                    TrainingType newType = new TrainingType();
                    newType.setName(name);
                    trainingTypeDao.save(newType);
                    return newType;
                });
    }
}