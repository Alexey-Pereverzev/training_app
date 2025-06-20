package org.example.trainingapp.service;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;

public interface AuthenticationService {
    Role validateCredentials(String username, String password);
    String validateCredentials(CredentialsDto credentialsDto);
}
