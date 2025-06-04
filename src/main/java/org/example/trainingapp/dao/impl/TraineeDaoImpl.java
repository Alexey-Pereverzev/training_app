package org.example.trainingapp.dao.impl;

import jakarta.transaction.Transactional;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class TraineeDaoImpl implements TraineeDao {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void save(Trainee trainee) {
        sessionFactory.getCurrentSession().persist(trainee);
    }

    @Override
    public void update(Trainee trainee) {
        sessionFactory.getCurrentSession().merge(trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Trainee trainee = sessionFactory.getCurrentSession().get(Trainee.class, id);
        return Optional.ofNullable(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from Trainee", Trainee.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Trainee trainee = sessionFactory.getCurrentSession().get(Trainee.class, id);
        if (trainee!=null) {
            sessionFactory.getCurrentSession().remove(trainee);
        }
    }
}
