package org.example.trainingapp.service;

import org.example.trainingapp.dto.ChangePasswordDto;

public interface UserService {
    void changePassword(String authHeader, ChangePasswordDto changePasswordDto);
}
