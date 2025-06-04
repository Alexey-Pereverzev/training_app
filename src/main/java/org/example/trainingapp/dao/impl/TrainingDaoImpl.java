package org.example.trainingapp.dao.impl;

import jakarta.transaction.Transactional;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.Training;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class TrainingDaoImpl implements TrainingDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Training training) {
        sessionFactory.getCurrentSession().persist(training);
    }

    @Override
    public void update(Training training) {
        sessionFactory.getCurrentSession().merge(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        Training training = sessionFactory.getCurrentSession().get(Training.class, id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Training", Training.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Training training = sessionFactory.getCurrentSession().get(Training.class, id);
        if (training!=null) {
            sessionFactory.getCurrentSession().remove(training);
        }
    }
}