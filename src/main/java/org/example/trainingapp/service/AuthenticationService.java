package org.example.trainingapp.service;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.JwtResponse;


public interface AuthenticationService {
    JwtResponse authorize(String username, String password);
    Role getRole(String username, String password);
    JwtResponse authorize(CredentialsDto credentialsDto);
    void logout();
}
