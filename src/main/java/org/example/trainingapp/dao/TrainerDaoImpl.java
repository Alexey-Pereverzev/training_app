package org.example.trainingapp.dao;

import org.example.trainingapp.entity.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private Map<Long, Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void save(Trainer trainer) {
        trainerStorage.put(trainer.getId(), trainer);
    }

    @Override
    public void update(Trainer trainer) {
        trainerStorage.put(trainer.getId(), trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public void deleteById(Long id) {
        trainerStorage.remove(id);
    }
}
