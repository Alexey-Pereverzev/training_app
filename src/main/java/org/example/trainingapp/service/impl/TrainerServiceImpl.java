package org.example.trainingapp.service.impl;

import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.util.CredentialsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = Logger.getLogger(TrainerServiceImpl.class.getName());
    private final TrainerDao trainerDao;

    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public void createTrainer(Trainer trainer) {
        List<String> existingUsernames = trainerDao.findAll().stream()
                .map(Trainer::getUsername)
                .toList();

        String username = CredentialsUtil.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        trainerDao.save(trainer);
        log.info("Trainer created: " + trainer.getUsername());
    }


    @Override
    public void updateTrainer(Trainer trainer) {
        trainerDao.update(trainer);
        log.info("Trainer updated: " + trainer.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainer(Long id) {
        Optional<Trainer> trainerOpt = trainerDao.findById(id);
        if (trainerOpt.isEmpty()) {
            log.warning("Trainer not found: ID=" + id);
        }
        return trainerOpt.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}
