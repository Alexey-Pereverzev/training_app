package org.example.trainingapp.util;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CredentialsUtilTest {

    @Test
    void whenUsernameExists_shouldGenerateUniqueUsernameWithSuffix() {
        // given
        List<String> existing = List.of("Ivan.Petrov", "Ivan.Petrov1", "Ivan.Petrov2");
        // when
        String username = CredentialsUtil.generateUsername("Ivan", "Petrov", existing);
        // then
        assertThat(username).isEqualTo("Ivan.Petrov3");
    }


    @Test
    void whenNoUsernameExists_shouldGenerateBasicUsername() {
        // given
        List<String> existing = List.of();
        // when
        String username = CredentialsUtil.generateUsername("Anna", "Ivanova", existing);
        // then
        assertThat(username).isEqualTo("Anna.Ivanova");
    }


    @Test
    void whenGeneratingPassword_shouldMatchLengthAndCharset() {
        // given
        int length = 10;
        // when
        String password = CredentialsUtil.generatePassword(length);
        // then
        assertThat(password.length()).isEqualTo(length);
        assertThat(password).matches("[A-Za-z0-9]+")
                .withFailMessage("Password should contain only alphanumeric characters");
    }


    @Test
    void whenGeneratingMultiplePasswords_shouldBeDifferent() {
        // when
        String p1 = CredentialsUtil.generatePassword(10);
        String p2 = CredentialsUtil.generatePassword(10);
        // then
        assertThat(p1).isNotEqualTo(p2).withFailMessage("Generated passwords should be different");
    }
}
