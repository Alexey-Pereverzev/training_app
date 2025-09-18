package org.example.trainingapp.bdd;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.example.trainingapp.constant.Constant.BEARER;


@Component
@RequiredArgsConstructor
public class MockMvcAuthHelper {

    public RequestPostProcessor bearer(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT token must not be blank");
        }
        final String headerValue = BEARER + token;
        return request -> {
            request.addHeader(org.springframework.http.HttpHeaders.AUTHORIZATION, headerValue);
            return request;
        };
    }
}
