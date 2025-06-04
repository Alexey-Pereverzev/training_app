package org.example.trainingapp.dao.impl;

import jakarta.transaction.Transactional;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TrainerDaoImpl implements TrainerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Trainer trainer) {
        sessionFactory.getCurrentSession().persist(trainer);
    }

    @Override
    public void update(Trainer trainer) {
        sessionFactory.getCurrentSession().merge(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Trainer trainer = sessionFactory.getCurrentSession().get(Trainer.class, id);
        return Optional.ofNullable(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Trainer", Trainer.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Trainer trainer = sessionFactory.getCurrentSession().get(Trainer.class, id);
        if (trainer!=null) {
            sessionFactory.getCurrentSession().remove(trainer);
        }
    }
}
