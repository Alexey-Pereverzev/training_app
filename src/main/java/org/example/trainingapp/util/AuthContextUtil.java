package org.example.trainingapp.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public class AuthContextUtil {

    private final AuthenticationService authenticationService;

    public AuthContextUtil(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String getRawAuthHeader() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            throw new SecurityException("No request context.");
        }
        HttpServletRequest request = attr.getRequest();
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new SecurityException("Missing or invalid Authorization header.");
        }
        return header;
    }

    public CredentialsDto getCredentials() {
        return AuthUtil.decodeBasicAuth(getRawAuthHeader());
    }

    public String getUsername() {
        return getCredentials().getUsername();
    }

    public Role getRole() {
        return authenticationService.authorize(getCredentials());
    }
}


