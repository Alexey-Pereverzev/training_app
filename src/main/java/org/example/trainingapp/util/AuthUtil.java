package org.example.trainingapp.util;

import org.example.trainingapp.dto.CredentialsDto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class AuthUtil {

    public static CredentialsDto decodeBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] parts = decodedString.split(":", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new SecurityException("Invalid credentials format");
        }
        return CredentialsDto.builder()
                .username(parts[0])
                .password(parts[1])
                .build();
    }
}
