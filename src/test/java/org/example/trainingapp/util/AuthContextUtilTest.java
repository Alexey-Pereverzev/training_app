package org.example.trainingapp.util;

import org.example.trainingapp.aspect.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class AuthContextUtilTest {

    @InjectMocks
    AuthContextUtil authContextUtil;


    @Test
    void whenGetRole_shouldCallAuthenticationService() {
        // given
        UserDetails userDetails = User.withUsername("Aliya.Aliyeva")
                .password("password")
                .roles("TRAINEE")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        // when
        Role role = authContextUtil.getRole();
        // then
        assertThat(role).isEqualTo(Role.TRAINEE);
    }


    @Test
    void whenGetRole_andNoAuthorities_shouldThrowException() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null,
                Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        // when + then
        assertThatThrownBy(() -> authContextUtil.getRole())
                .isInstanceOf(SecurityException.class)
                .hasMessage("No authorities found");
    }


    @Test
    void whenGetUsername_shouldReturnAuthenticatedUsername() {
        // given
        UserDetails userDetails = User.withUsername("Aliya.Aliyeva")
                .password("password")
                .roles("TRAINEE")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        // when
        String username = authContextUtil.getUsername();
        // then
        assertThat(username).isEqualTo("Aliya.Aliyeva");
    }


    @Test
    void whenGetUsername_notAuthenticated_shouldThrowException() {                  //  testing getAuthentication()
        // given
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(null);
        SecurityContextHolder.setContext(context);
        // when + then
        assertThatThrownBy(() -> authContextUtil.getUsername())
                .isInstanceOf(SecurityException.class)
                .hasMessage("User is not authenticated");
    }
}

