package org.example.trainingapp.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.JwtResponse;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.filter.AuthTokenFilter;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.example.trainingapp.jwt.TokenBlacklistUtil;
import org.example.trainingapp.repository.UserRepository;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class JpaAuthenticationService implements AuthenticationService {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final AuthTokenFilter authTokenFilter;
    private final TokenBlacklistUtil tokenBlacklistUtil;
    private static final Logger log = LoggerFactory.getLogger(JpaAuthenticationService.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);
    private static final Duration ATTEMPT_RESET_DURATION = Duration.ofMinutes(10);


    @Override
    @Transactional
    public JwtResponse authorize(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)     //  1. searching by username in DB
                .orElseThrow(() -> {
                    log.warn("Authentication failed: Invalid username for user '{}'", username);
                    return new UsernameNotFoundException("User not found");
                });

        if (user.isAccountLocked()) {                           //  2. checking if account is locked
            log.warn("Authentication failed: account {} locked, please try again in 5 minutes", username);
            throw new SecurityException("Account is temporarily locked. Try again in 5 minutes.");
        }

        if (user.getLastFailedLogin() != null &&                //  3. resetting failed attempts counter after 10 minutes
                user.getLastFailedLogin().isBefore(LocalDateTime.now().minus(ATTEMPT_RESET_DURATION))) {
            log.info("Failed attempts reset");
            user.setFailedAttempts(0);
        }
                                                                //  4. checking credentials
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            user.setLastFailedLogin(LocalDateTime.now());
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {              //  blocking user after 3 attempts
                user.setLockTime(LocalDateTime.now().plus(BLOCK_DURATION));
            }
            userRepository.save(user);
            log.warn("Authentication failed: Invalid password for user '{}'", username);
            throw new SecurityException("Invalid credentials");
        }

        String roleName = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No role assigned to user '{}'", username);
                    return new SecurityException("No role assigned");
                })
                .getAuthority();                                // 5. assigning role
        try {                                                   // 6. generating token
           String token = jwtTokenUtil.generateToken(userDetails);
            Role role = Role.valueOf(roleName.replace("ROLE_", ""));    // convert role to enum
            user.setFailedAttempts(0);                                                  // reset failed attempts
            user.setLastFailedLogin(null);
            user.setLockTime(null);
            userRepository.save(user);
            log.info("{} authentication successful: {}", role.name(), username);
            return new JwtResponse(token, role.name());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("Generating token error: {}", e.getMessage());
            throw new SecurityException("Cannot validate token: " + e.getMessage());
        }
    }


    @Override
    public void logout() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            log.warn("No request context");
            throw new SecurityException("No request context.");
        }
        HttpServletRequest request = attrs.getRequest();
        String token = authTokenFilter.getTokenFromJwt(request);
        Instant expirationTime = jwtTokenUtil.getTokenExpiration(token);
        tokenBlacklistUtil.blacklistToken(token, expirationTime);
    }


    @Override
    public Role getRole(String username, String password) {
        JwtResponse jwtResponse = authorize(username, password);
        return Role.valueOf(jwtResponse.getRole());
    }


    @Override
    public JwtResponse authorize(CredentialsDto credentialsDto) {
        ValidationUtils.validateCredentials(credentialsDto);
        return authorize(credentialsDto.getUsername(), credentialsDto.getPassword());
    }
}
