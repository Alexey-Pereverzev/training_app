package org.example.trainingapp.util;

import org.example.trainingapp.aspect.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class AuthContextUtil {

    private static final Logger log = LoggerFactory.getLogger(AuthContextUtil.class.getName());


    public String getUsername() {
        Authentication auth = getAuthentication();
        return auth.getName();
    }

    public Role getRole() {
        Authentication auth = getAuthentication();
        String authority = auth.getAuthorities().stream().findFirst()
                .orElseThrow(() -> {
                    log.warn("No authorities found during getting role");
                    return new SecurityException("No authorities found");
                }).getAuthority();
        return Role.valueOf(authority.replace("ROLE_", ""));
    }


    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("User is not authenticated");
            throw new SecurityException("User is not authenticated");
        }
        return auth;
    }
}


