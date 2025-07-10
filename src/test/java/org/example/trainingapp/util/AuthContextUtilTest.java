package org.example.trainingapp.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthContextUtilTest {

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    AuthContextUtil authContextUtil;

    @Test
    void whenGetRawAuthHeader_validHeader_shouldReturnUsername() {
        // given
        var header = "Basic " + Base64.getEncoder().encodeToString("Aliya.Aliyeva:password".getBytes());
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(header);
        var attr = mock(ServletRequestAttributes.class);
        when(attr.getRequest()).thenReturn(request);
        // when
        RequestContextHolder.setRequestAttributes(attr);
        // then
        assertThat(authContextUtil.getUsername()).isEqualTo("Aliya.Aliyeva");
    }

    @Test
    void whenGetRawAuthHeader_noRequestContext_shouldThrowSecurityException() {
        // given
        RequestContextHolder.resetRequestAttributes();
        // when + then
        assertThatThrownBy(() -> authContextUtil.getRawAuthHeader())
                .isInstanceOf(SecurityException.class)
                .hasMessage("No request context.");
    }

    @Test
    void whenGetRawAuthHeader_invalidBearerHeader_shouldThrowSecurityException() {
        // given
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        var attr = mock(ServletRequestAttributes.class);
        when(attr.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attr);
        // when + then
        assertThatThrownBy(() -> authContextUtil.getRawAuthHeader())
                .isInstanceOf(SecurityException.class)
                .hasMessage("Missing or invalid Authorization header.");
    }

    @Test
    void whenGetRole_shouldCallAuthenticationService() {
        // given
        var header = "Basic " + Base64.getEncoder().encodeToString("Aliya.Aliyeva:password".getBytes());
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(header);
        var attr = mock(ServletRequestAttributes.class);
        when(attr.getRequest()).thenReturn(request);
        when(authenticationService.authorize(any())).thenReturn(Role.TRAINEE    );
        // when
        RequestContextHolder.setRequestAttributes(attr);
        // then
        assertThat(authContextUtil.getRole()).isEqualTo(Role.TRAINEE);
    }
}

