package org.example.trainingapp.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.example.trainingapp.jwt.TokenBlacklistUtil;
import org.example.trainingapp.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistUtil tokenBlacklistUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void whenDoFilterInternal_validToken_shouldAuthenticateUser() throws Exception {
        // given
        String token = "valid.jwt.token";
        String username = "Aliya.Aliyeva";
        doReturn("Bearer " + token).when(request).getHeader("Authorization");
        doReturn(false).when(tokenBlacklistUtil).isTokenBlacklisted(token);
        DecodedJWT mockJwt = mock(DecodedJWT.class);
        doReturn(mockJwt).when(jwtTokenUtil).validateAndParseToken(token);
        doReturn(username).when(jwtTokenUtil).getUsernameFromToken(token);
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(username);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_TRAINER"))).when(userDetails).getAuthorities();
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userDetails, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> "ROLE_TRAINER".equals(a.getAuthority())));
        verify(filterChain).doFilter(request, response);
    }


    @Test
    void whenDoFilterInternal_tokenIsBlacklisted_shouldThrowAndSkipAuthentication() throws Exception {
        // given
        String token = "blacklisted.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistUtil.isTokenBlacklisted(token)).thenReturn(true);
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response); // всё равно передаётся дальше
    }


    @Test
    void whenDoFilterInternal_tokenInvalid_shouldNotAuthenticateUser() throws Exception {
        // given
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenBlacklistUtil.isTokenBlacklisted(token)).thenReturn(false);
        doThrow(new RuntimeException("Invalid")).when(jwtTokenUtil).validateAndParseToken(token);
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }


    @Test
    void whenDoFilterInternal_noToken_shouldJustContinue() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);
        // when
        authTokenFilter.doFilterInternal(request, response, filterChain);
        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }


    @Test
    void whenGetTokenFromJwt_headerMissing_shouldReturnNull() {
        // when + then
        assertNull(authTokenFilter.getTokenFromJwt(request));
    }


    @Test
    void whenGetTokenFromJwt_withCorrectHeader_shouldExtractToken() {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer my.token.value");
        // when
        String token = authTokenFilter.getTokenFromJwt(request);
        // then
        assertEquals("my.token.value", token);
    }
}

