package org.example.trainingapp.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CredentialsUtilTest {

    @Test
    void whenUsernameExists_shouldGenerateUniqueUsernameWithSuffix() {
        // given
        long count = 3L;
        // when
        String username = CredentialsUtil.generateUsername("Ivan", "Petrov", count);
        // then
        assertThat(username).isEqualTo("Ivan.Petrov3");
    }


    @Test
    void whenNoUsernameExists_shouldGenerateBasicUsername() {
        // given
        long count = 0L;
        // when
        String username = CredentialsUtil.generateUsername("Anna", "Ivanova", count);
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
