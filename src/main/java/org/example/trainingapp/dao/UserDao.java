package org.example.trainingapp.dao;

public interface UserDao {
    long countUsersByNameAndSurname(String firstName, String lastName);
}
