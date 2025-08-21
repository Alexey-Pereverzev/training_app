package org.example.trainingapp.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.JwtResponse;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.filter.AuthTokenFilter;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.example.trainingapp.jwt.TokenBlacklistUtil;
import org.example.trainingapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JpaAuthenticationServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes attributes;

    @Mock
    private AuthTokenFilter authTokenFilter;

    @Mock
    private TokenBlacklistUtil tokenBlacklistUtil;

    @InjectMocks
    private JpaAuthenticationService authService;


    @Test
    void whenTrainerValid_shouldReturnTrainerRole() {
        // given
        User user = new Trainer();
        user.setUsername("trainer1");
        user.setPassword("pw123");
        user.setFailedAttempts(0);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("trainer1")
                .password("pw123")
                .roles("TRAINER")
                .build();
        when(userDetailsService.loadUserByUsername("trainer1")).thenReturn(userDetails);
        when(userRepository.findByUsername("trainer1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw123", user.getPassword())).thenReturn(true);
        // when
        JwtResponse jwtResponse = authService.authorize("trainer1", "pw123");
        Role role = Role.valueOf(jwtResponse.getRole());
        // then
        assertThat(role).isEqualTo(Role.TRAINER);
    }


    @Test
    void whenTraineeValid_shouldReturnTraineeRole() {
        // given
        User user = new Trainee();
        user.setUsername("trainee1");
        user.setPassword("pw456");
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("trainee1")
                .password("pw456")
                .roles("TRAINEE")
                .build();
        when(userDetailsService.loadUserByUsername("trainee1")).thenReturn(userDetails);
        when(userRepository.findByUsername("trainee1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw456", user.getPassword())).thenReturn(true);
        // when
        JwtResponse jwtResponse = authService.authorize("trainee1", "pw456");
        Role role = Role.valueOf(jwtResponse.getRole());
        // then
        assertThat(role).isEqualTo(Role.TRAINEE);
    }


    @Test
    void whenInvalidCredentials_shouldThrowException() {
        // when + then
        assertThatThrownBy(() ->
                authService.authorize("invalid", "wrongpass"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }


    @Test
    void whenAuthorize_accountIsLocked_shouldThrowSecurityException() {
        // given
        User user = new Trainee();
        user.setUsername("blocked");
        user.setPassword("pw");
        user.setLockTime(LocalDateTime.now().plusMinutes(3));
        when(userRepository.findByUsername("blocked")).thenReturn(Optional.of(user));
        // when + then
        assertThatThrownBy(() -> authService.authorize("blocked", "pw"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Account is temporarily locked");
    }


    @Test
    void whenAuthorize_passwordInvalid_shouldIncreaseAttemptsAndThrow() {
        // given
        User user = new Trainee();
        user.setUsername("user1");
        user.setPassword("hashed");
        user.setFailedAttempts(1);
        user.setLastFailedLogin(LocalDateTime.now().minusMinutes(1));
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user1")
                .password("hashed")
                .roles("TRAINEE")
                .build();
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        // when + then
        assertThatThrownBy(() -> authService.authorize("user1", "wrong"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid credentials");
        verify(userRepository).save(argThat(saved -> saved.getFailedAttempts() == 2));
    }


    @Test
    void whenAuthorize_noRoleAssigned_shouldThrowException() {
        // given
        User user = new Trainer();
        user.setUsername("user");
        user.setPassword("pw");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user", "pw", Collections.emptyList());
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(passwordEncoder.matches("pw", "pw")).thenReturn(true);
        // when + then
        assertThatThrownBy(() -> authService.authorize("user", "pw"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("No role assigned");
    }


    @Test
    void whenAuthorize_withCredentialsDto_shouldWorkSameAsDirectCall() throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // given
        User user = new Trainee();
        user.setUsername("test");
        user.setPassword("pw");
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test")
                .password("pw")
                .roles("TRAINEE")
                .build();
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("test")).thenReturn(userDetails);
        when(passwordEncoder.matches("pw", "pw")).thenReturn(true);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt-token");
        // when
        JwtResponse response = authService.authorize(new CredentialsDto("test", "pw"));
        // then
        assertThat(response.getRole()).isEqualTo("TRAINEE");
    }


    @Test
    void whenLogout_shouldExtractTokenAndBlacklistIt() {
        // given
        String token = "mocked.jwt.token";
        Instant exp = Instant.now().plusSeconds(3600);
        when(authTokenFilter.getTokenFromJwt(request)).thenReturn(token);
        when(jwtTokenUtil.getTokenExpiration(token)).thenReturn(exp);
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        // when
        authService.logout();
        // then
        verify(tokenBlacklistUtil).blacklistToken(eq(token), eq(exp));
    }
}

