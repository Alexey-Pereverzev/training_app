package org.example.trainingapp.util;

import java.security.SecureRandom;
import java.util.List;


public class CredentialsUtil {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String base = firstName + "." + lastName;
        String candidate = base;
        int counter = 1;
        while (existingUsernames.contains(candidate)) {
            candidate = base + counter;
            counter++;
        }
        return candidate;
    }
}
