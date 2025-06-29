package org.example.trainingapp.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.example.trainingapp.dao.UserDao;
import org.springframework.stereotype.Repository;


@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }


    @Override
    public long countUsersByNameAndSurname(String firstName, String lastName) {
        try (EntityManager em = entityManager()) {
            return em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName", Long.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getSingleResult();
        }
    }

}

