package org.example.trainingapp.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.service.impl.DaoAuthenticationService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Repository
public class TraineeDaoImpl implements TraineeDao {

    private static final Logger logger = Logger.getLogger(DaoAuthenticationService.class.getName());

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void save(Trainee trainee) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(trainee);
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
    public void update(Trainee trainee) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(trainee);
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
    public Optional<Trainee> findById(Long id) {
        try (EntityManager em = entityManager()) {
            return Optional.ofNullable(em.find(Trainee.class, id));
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try (EntityManager em = entityManager()) {
            TypedQuery<Trainee> query = em.createQuery("FROM Trainee WHERE username = :username", Trainee.class);
            query.setParameter("username", username);
            return query.getResultList().stream().findFirst();
        }
    }

    @Override
    public Optional<Trainee> findByUsernameWithTrainings(String username) {
        try (EntityManager em = entityManager()) {
            TypedQuery<Trainee> query = em.createQuery(
                    "SELECT t FROM Trainee t LEFT JOIN FETCH t.trainings WHERE t.username = :username", Trainee.class);
            query.setParameter("username", username);
            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Trainee> findAll() {
        try (EntityManager em = entityManager()) {
            return em.createQuery("FROM Trainee", Trainee.class).getResultList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try (EntityManager em = entityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Trainee trainee = em.find(Trainee.class, id);
                if (trainee != null) {
                    em.remove(trainee);
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
