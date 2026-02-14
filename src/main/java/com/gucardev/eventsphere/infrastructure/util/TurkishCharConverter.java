package com.gucardev.eventsphere.infrastructure.util;

import java.util.HashMap;
import java.util.Map;

public class TurkishCharConverter {

    private static final Map<Character, Character> TURKISH_TO_ENGLISH = new HashMap<>();

    static {
        TURKISH_TO_ENGLISH.put('ç', 'c');
        TURKISH_TO_ENGLISH.put('Ç', 'C');
        TURKISH_TO_ENGLISH.put('ğ', 'g');
        TURKISH_TO_ENGLISH.put('Ğ', 'G');
        TURKISH_TO_ENGLISH.put('ı', 'i');
        TURKISH_TO_ENGLISH.put('İ', 'I');
        TURKISH_TO_ENGLISH.put('ö', 'o');
        TURKISH_TO_ENGLISH.put('Ö', 'O');
        TURKISH_TO_ENGLISH.put('ş', 's');
        TURKISH_TO_ENGLISH.put('Ş', 'S');
        TURKISH_TO_ENGLISH.put('ü', 'u');
        TURKISH_TO_ENGLISH.put('Ü', 'U');
    }

    public static String convertTurkishToEnglish(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            Character replacement = TURKISH_TO_ENGLISH.get(c);
            result.append(replacement != null ? replacement : c);
        }

        return result.toString();
    }

    public static String toEnglishUpperCase(String input) {
        String converted = convertTurkishToEnglish(input);
        return converted.toUpperCase();
    }

    public static String toEnglishLowerCase(String input) {
        String converted = convertTurkishToEnglish(input);
        return converted.toLowerCase();
    }
}
