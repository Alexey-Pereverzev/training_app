package org.example.trainingapp.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Repository
public class TrainerDaoImpl implements TrainerDao {

    private static final Logger logger = Logger.getLogger(TrainerDaoImpl.class.getName());

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void save(Trainer trainer) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(trainer);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                logger.severe("Transaction failed: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public void update(Trainer trainer) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(trainer);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                logger.severe("Transaction failed: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        try (EntityManager em = entityManager()) {
            return Optional.ofNullable(em.find(Trainer.class, id));
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try (EntityManager em = entityManager()) {
            TypedQuery<Trainer> query = em.createQuery("FROM Trainer WHERE username = :username", Trainer.class);
            query.setParameter("username", username);
            return query.getResultList().stream().findFirst();
        }
    }

    @Override
    public Optional<Trainer> findByUsernameWithTrainings(String username) {
        try (EntityManager em = entityManager()) {
            TypedQuery<Trainer> query = em.createQuery(
                    "SELECT t FROM Trainer t LEFT JOIN FETCH t.trainings WHERE t.username = :username", Trainer.class);
            query.setParameter("username", username);
            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Trainer> findAll() {
        try (EntityManager em = entityManager()) {
            return em.createQuery("FROM Trainer", Trainer.class).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Trainer trainer = em.find(Trainer.class, id);
                if (trainer != null) {
                    em.remove(trainer);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                logger.severe("Transaction failed: " + e.getMessage());
                throw e;
            }
        }
    }
}
