package org.example.trainingapp.dao;

import java.util.Set;

public interface UserDao {
    Set<String> findUsernamesByNameAndSurname(String firstName, String lastName);
}
