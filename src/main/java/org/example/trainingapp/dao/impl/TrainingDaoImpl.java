package org.example.trainingapp.dao.impl;

import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    private Map<Long, Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void save(Training training) {
        trainingStorage.put(training.getId(), training);
    }

    @Override
    public void update(Training training) {
        trainingStorage.put(training.getId(), training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(trainingStorage.values());
    }

    @Override
    public void deleteById(Long id) {
        trainingStorage.remove(id);
    }
}