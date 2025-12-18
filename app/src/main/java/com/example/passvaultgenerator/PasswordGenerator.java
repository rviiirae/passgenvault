package com.example.passvaultgenerator;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final String SYMBOL_CHARACTERS = "!@#$%^&*()-_=+";

    public static String generatePassword(int length, boolean useLowercase, boolean useUppercase, boolean useNumeric, boolean useSymbols) {
        StringBuilder allowedCharacters = new StringBuilder();
        if (useLowercase) {
            allowedCharacters.append(LOWERCASE_CHARACTERS);
        }
        if (useUppercase) {
            allowedCharacters.append(UPPERCASE_CHARACTERS);
        }
        if (useNumeric) {
            allowedCharacters.append(NUMERIC_CHARACTERS);
        }
        if (useSymbols) {
            allowedCharacters.append(SYMBOL_CHARACTERS);
        }

        if (allowedCharacters.length() == 0) {
            return ""; // No character types selected
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(allowedCharacters.charAt(random.nextInt(allowedCharacters.length())));
        }
        return password.toString();
    }
}
