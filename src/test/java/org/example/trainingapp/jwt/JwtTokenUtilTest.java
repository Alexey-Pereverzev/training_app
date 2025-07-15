package org.example.trainingapp.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    @TempDir
    Path tempDir;                                       //  temp directory for RSA keys
    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() throws Exception {
        RsaKeyGenerator gen = new RsaKeyGenerator(tempDir.toString());
        gen.generateKeyPair();
        jwtTokenUtil = new JwtTokenUtil(tempDir.toString(), 3600000);
    }


    @Test
    void whenGenerateToken_validatedSuccessfully() throws Exception {
        // given
        UserDetails userDetails = User.withUsername("Dina.Aliyeva")
                .password("pw123")
                .roles("TRAINER")
                .build();
        // when
        String token = jwtTokenUtil.generateToken(userDetails);
        // then
        DecodedJWT jwt = jwtTokenUtil.validateAndParseToken(token);
        assertEquals("Dina.Aliyeva", jwt.getSubject());
        assertEquals("ROLE_TRAINER", jwt.getClaim("role").asString());
    }


    @Test
    void whenValidateAndParseToken_invalidToken_shouldThrowException() {
        // given
        String invalidToken = "this.is.not.valid";
        // when + then
        assertThrows(JWTVerificationException.class, () -> jwtTokenUtil.validateAndParseToken(invalidToken));
    }


    @Test
    void whenGetUsernameFromToken_correctUsername() throws Exception {
        // given
        String token = generateTestToken();
        // when
        String username = jwtTokenUtil.getUsernameFromToken(token);
        // then
        assertEquals("Dina.Aliyeva", username);
    }


    @Test
    void whenGetRoleFromToken_correctRole() throws Exception {
        // given
        String token = generateTestToken();
        // when
        String role = jwtTokenUtil.getRole(token);
        // then
        assertEquals("ROLE_TRAINER", role);
    }


    @Test
    void whenGetExpiration_isAfterNow() throws Exception {
        // given
        String token = generateTestToken();
        // when
        Instant expiration = jwtTokenUtil.getTokenExpiration(token);
        // then
        assertTrue(expiration.isAfter(Instant.now()));
    }


    private String generateTestToken() throws Exception {
        UserDetails userDetails = User.withUsername("Dina.Aliyeva")
                .password("pw123")
                .roles("TRAINER")
                .build();
        return jwtTokenUtil.generateToken(userDetails);
    }
}

