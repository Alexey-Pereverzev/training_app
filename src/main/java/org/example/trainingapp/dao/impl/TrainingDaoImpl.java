package org.example.trainingapp.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnit;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.Training;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class TrainingDaoImpl implements TrainingDao {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }


    @Override
    public void save(Training training) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(training);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }


    @Override
    public void update(Training training) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(training);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }


    @Override
    public Optional<Training> findById(Long id) {
        try (EntityManager em = entityManager()) {
            return Optional.ofNullable(em.find(Training.class, id));
        }
    }


    @Override
    public List<Training> findAll() {
        try (EntityManager em = entityManager()) {
            return em.createQuery("FROM Training", Training.class).getResultList();
        }
    }


    @Override
    public void deleteById(Long id) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Training training = em.find(Training.class, id);
                if (training != null) {
                    em.remove(training);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

}

