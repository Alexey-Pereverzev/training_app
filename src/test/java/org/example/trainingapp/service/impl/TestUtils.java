package org.example.trainingapp.service.impl;

import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.util.AuthUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.anyString;


public class TestUtils {
    public static String createAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String base64 = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64;
    }

    public static MockedStatic<AuthUtil> mockDecodeAuth(String username, String password) {
        MockedStatic<AuthUtil> mockedStatic = Mockito.mockStatic(AuthUtil.class);
        mockedStatic.when(() -> AuthUtil.decodeBasicAuth(anyString()))
                .thenReturn(new CredentialsDto(username, password));
        return mockedStatic;
    }
}
