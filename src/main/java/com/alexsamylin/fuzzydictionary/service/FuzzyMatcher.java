package com.alexsamylin.fuzzydictionary.service;

import org.springframework.stereotype.Component;

@Component
public class FuzzyMatcher {

    public boolean isFuzzyMatch(String word1, String word2) {
        if (word1 == null || word2 == null) {
            return false;
        }

        if (word1.length() != word2.length()) {
            return false;
        }

        if (word1.equals(word2)) {
            return true;
        }

        int diffCount = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                diffCount++;
                if (diffCount > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    public String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.toLowerCase().replaceAll("[^a-z]", "");
    }
}
