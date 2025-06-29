package org.example.trainingapp.util;

import java.security.SecureRandom;
import java.util.Set;


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

    public static String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String base = firstName + "." + lastName;
        if (!existingUsernames.contains(base)) {
            return base;
        }
        for (int i = 1; i < Integer.MAX_VALUE; i++) {           //  searching for unique username non-present in DB
            String candidate = base + i;
            if (!existingUsernames.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to generate unique username for " + base);
    }
}
