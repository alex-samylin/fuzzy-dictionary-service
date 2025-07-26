package com.alexsamylin.fuzzydictionary.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyMatcherTest {

    private final FuzzyMatcher matcher = new FuzzyMatcher();

    @Test
    void shouldReturnTrueWhenWordsAreExactlyTheSame() {
        assertTrue(matcher.isFuzzyMatch("test", "test"));
    }

    @Test
    void shouldReturnTrueWhenWordsDifferByOneCharacter() {
        assertTrue(matcher.isFuzzyMatch("test", "tent"));
    }

    @Test
    void shouldReturnFalseWhenWordsDifferByMoreThanOneCharacter() {
        assertFalse(matcher.isFuzzyMatch("test", "team"));
    }

    @Test
    void shouldReturnFalseWhenWordsHaveDifferentLengths() {
        assertFalse(matcher.isFuzzyMatch("test", "tests"));
    }

    @Test
    void shouldReturnFalseWhenEitherWordIsNull() {
        assertFalse(matcher.isFuzzyMatch(null, "test"));
        assertFalse(matcher.isFuzzyMatch("test", null));
        assertFalse(matcher.isFuzzyMatch(null, null));
    }

    @Test
    void shouldRemoveNonAlphabeticCharactersInNormalize() {
        assertEquals("abc", matcher.normalize("a@#b1c2"));
    }

    @Test
    void shouldConvertToLowerCaseInNormalize() {
        assertEquals("test", matcher.normalize("TeSt"));
    }

    @Test
    void shouldReturnEmptyStringWhenNormalizingNull() {
        assertEquals("", matcher.normalize(null));
    }

    @Test
    void shouldReturnEmptyStringWhenNormalizingNonLatinInput() {
        assertEquals("", matcher.normalize("123!@#абв"));
    }
}
