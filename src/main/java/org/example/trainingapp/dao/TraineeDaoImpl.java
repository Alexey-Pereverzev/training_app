package org.example.trainingapp.dao;

import org.example.trainingapp.entity.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class TraineeDaoImpl implements TraineeDao {

    private Map<Long, Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void save(Trainee trainee) {
        traineeStorage.put(trainee.getId(), trainee);
    }

    @Override
    public void update(Trainee trainee) {
        traineeStorage.put(trainee.getId(), trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public void deleteById(Long id) {
        traineeStorage.remove(id);
    }
}
