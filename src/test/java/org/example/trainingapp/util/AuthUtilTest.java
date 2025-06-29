package org.example.trainingapp.util;

import org.example.trainingapp.dto.CredentialsDto;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class AuthUtilTest {

    @Test
    void whenValidAuthHeader_shouldReturnCredentials() {
        // given
        String username = "user";
        String password = "pass123";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        // when
        CredentialsDto creds = AuthUtil.decodeBasicAuth(authHeader);
        // then
        assertThat(creds.getUsername()).isEqualTo(username);
        assertThat(creds.getPassword()).isEqualTo(password);
    }


    @Test
    @SuppressWarnings("DataFlowIssue")
    void whenNullHeader_shouldThrowSecurityException() {
        // when + then
        assertThatThrownBy(() -> AuthUtil.decodeBasicAuth(null))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Missing or invalid");
    }


    @Test
    void whenHeaderDoesNotStartWithBasic_shouldThrowSecurityException() {
        // given
        String invalidHeader = "Bearer sometoken";
        // when + then
        assertThatThrownBy(() -> AuthUtil.decodeBasicAuth(invalidHeader))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Missing or invalid");
    }


    @Test
    void whenHeaderHasInvalidBase64_shouldThrowIllegalArgumentException() {
        // given
        String invalidBase64 = "Basic !!not-base64!!";
        // when + then
        assertThatThrownBy(() -> AuthUtil.decodeBasicAuth(invalidBase64))
                .isInstanceOf(IllegalArgumentException.class); // thrown by Base64 decoder
    }


    @Test
    void whenHeaderLacksColon_shouldThrowSecurityException() {
        // given
        String encoded = Base64.getEncoder().encodeToString("invalidFormat".getBytes(StandardCharsets.UTF_8));
        String header = "Basic " + encoded;
        // when + then
        assertThatThrownBy(() -> AuthUtil.decodeBasicAuth(header))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid credentials format");
    }


    @Test
    void whenUsernameOrPasswordBlank_shouldThrowSecurityException() {
        // given
        String encoded = Base64.getEncoder().encodeToString(":blank".getBytes(StandardCharsets.UTF_8));
        String header = "Basic " + encoded;
        // when + then
        assertThatThrownBy(() -> AuthUtil.decodeBasicAuth(header))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid credentials format");
    }
}

