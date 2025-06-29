package org.example.trainingapp.util;

import java.security.SecureRandom;


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

    public static String generateUsername(String firstName, String lastName, long existingCount) {
        String base = firstName + "." + lastName;
        return existingCount == 0 ? base : base + existingCount;
    }
}
