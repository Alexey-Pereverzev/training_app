package org.example.trainingapp.util;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class CredentialsUtilTest {

    @Test
    void testGenerateUsername_unique() {
        List<String> existing = List.of("Ivan.Petrov", "Ivan.Petrov1", "Ivan.Petrov2");
        String username = CredentialsUtil.generateUsername("Ivan", "Petrov", existing);
        assertEquals("Ivan.Petrov3", username);
    }


    @Test
    void testGenerateUsername_firstTime() {
        List<String> existing = List.of();
        String username = CredentialsUtil.generateUsername("Anna", "Ivanova", existing);
        assertEquals("Anna.Ivanova", username);
    }


    @Test
    void testGeneratePassword_lengthAndCharset() {
        int length = 10;
        String password = CredentialsUtil.generatePassword(length);
        assertEquals(length, password.length());
        assertTrue(password.matches("[A-Za-z0-9]+"), "Password should contain only alphanumeric characters");
    }


    @Test
    void testGenerateMultiplePasswordsAreDifferent() {
        String p1 = CredentialsUtil.generatePassword(10);
        String p2 = CredentialsUtil.generatePassword(10);
        assertNotEquals(p1, p2, "Generated passwords should be different");
    }
}
