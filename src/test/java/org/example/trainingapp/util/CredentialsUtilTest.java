package org.example.trainingapp.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class CredentialsUtilTest {

    @Test
    void whenUsernameExists_shouldGenerateUniqueUsernameWithSuffix() {
        // given
        Set<String> existingUsernames = Set.of("Ivan.Petrov", "Ivan.Petrov1", "Ivan.Petrov2");
        // when
        String username = CredentialsUtil.generateUsername("Ivan", "Petrov", existingUsernames);
        // then
        assertThat(username).isEqualTo("Ivan.Petrov3");
    }


    @Test
    void whenNoUsernameExists_shouldGenerateBasicUsername() {
        // given
        Set<String> existingUsernames = Set.of();
        // when
        String username = CredentialsUtil.generateUsername("Anna", "Ivanova", existingUsernames);
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
        assertThat(password).withFailMessage("Password should contain only alphanumeric characters")
                .matches("[A-Za-z0-9]+");
    }


    @Test
    void whenGeneratingMultiplePasswords_shouldBeDifferent() {
        // when
        String p1 = CredentialsUtil.generatePassword(10);
        String p2 = CredentialsUtil.generatePassword(10);
        // then
        assertThat(p1).withFailMessage("Generated passwords should be different").isNotEqualTo(p2);
    }
}
