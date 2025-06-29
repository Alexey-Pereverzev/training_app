package org.example.trainingapp.service;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;

public interface AuthenticationService {
    Role authorize(String username, String password);
    Role authorize(CredentialsDto credentialsDto);
}
