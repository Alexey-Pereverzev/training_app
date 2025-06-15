package org.example.trainingapp.service;

import org.example.trainingapp.aspect.Role;

public interface AuthenticationService {
    Role validateCredentials(String username, String password);
}
