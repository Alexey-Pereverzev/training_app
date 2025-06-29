package org.example.trainingapp.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnit;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.entity.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }


    @Override
    public void save(TrainingType trainingType) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(trainingType);
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
    public void update(TrainingType trainingType) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(trainingType);
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
    public Optional<TrainingType> findById(Long id) {
        try (EntityManager em = entityManager()) {
            return Optional.ofNullable(em.find(TrainingType.class, id));
        }
    }


    @Override
    public List<TrainingType> findAll() {
        try (EntityManager em = entityManager()) {
            return em.createQuery("FROM TrainingType", TrainingType.class).getResultList();
        }
    }


    @Override
    public void deleteById(Long id) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                TrainingType type = em.find(TrainingType.class, id);
                if (type != null) {
                    em.remove(type);
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


    @Override
    public Optional<TrainingType> findByName(String name) {
        try (EntityManager em = entityManager()) {
            TrainingType type = em.createQuery("FROM TrainingType WHERE name = :name", TrainingType.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return Optional.ofNullable(type);
        }
    }

}
