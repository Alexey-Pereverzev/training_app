package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.util.CredentialsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = Logger.getLogger(TrainerServiceImpl.class.getName());

    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }


    @Override
    public void createTrainer(Trainer trainer) {
        List<String> existingUsernames = trainerDao.findAll().stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        String username = CredentialsUtil.generateUsername(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        User user = trainer.getUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        trainerDao.save(trainer);
        log.info("Trainer created: " + user.getUsername());
    }


    @Override
    public void updateTrainer(Trainer trainer) {
        trainerDao.update(trainer);
        log.info("Trainer updated: " + trainer.getId());
    }

    @Override
    public Trainer getTrainer(Long id) {
        Optional<Trainer> trainerOpt = trainerDao.findById(id);
        if (trainerOpt.isEmpty()) {
            log.warning("Trainer not found: ID=" + id);
        }
        return trainerOpt.orElse(null);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}
