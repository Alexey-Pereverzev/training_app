package org.example.trainingapp.dao.impl;

import jakarta.transaction.Transactional;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.entity.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(TrainingType trainingType) {
        sessionFactory.getCurrentSession().persist(trainingType);
    }

    @Override
    public void update(TrainingType trainingType) {
        sessionFactory.getCurrentSession().merge(trainingType);
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        TrainingType type = sessionFactory.getCurrentSession().get(TrainingType.class, id);
        return Optional.ofNullable(type);
    }

    @Override
    public List<TrainingType> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM TrainingType", TrainingType.class)
                .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        TrainingType type = sessionFactory.getCurrentSession().get(TrainingType.class, id);
        if (type != null) {
            sessionFactory.getCurrentSession().remove(type);
        }
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        TrainingType type = sessionFactory.getCurrentSession()
                .createQuery("FROM TrainingType WHERE name = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResult();
        return Optional.ofNullable(type);
    }
}
