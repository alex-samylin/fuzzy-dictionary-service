
package com.alexsamylin.fuzzydictionary.service;

import com.alexsamylin.fuzzydictionary.model.DictionaryRecord;
import com.alexsamylin.fuzzydictionary.error.InvalidSearchQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyDictionaryServiceTest {

    private FuzzyDictionaryService dictionaryService;

    @BeforeEach
    void setUp() {
        FuzzyMatcher matcher = new FuzzyMatcher();
        dictionaryService = new FuzzyDictionaryService(matcher);
        dictionaryService.setMaxQueryLength(255);

        List<DictionaryRecord> records = List.of(
                new DictionaryRecord(1, "abc ade rty"),
                new DictionaryRecord(2, "tyuif abe234 ghjjk5"),
                new DictionaryRecord(3, "fg 13fd")
        );

        dictionaryService.updateDictionary(records);
    }

    @Test
    void testSearchExactAndFuzzyMatch() {
        Set<Integer> result = dictionaryService.search("abc");
        assertEquals(Set.of(1, 2), result); // 1 - exact match, 2 - fuzzy match
    }

    @Test
    void testSearchWithExactOnly() {
        Set<Integer> result = dictionaryService.search("rty");
        assertEquals(Set.of(1), result);
    }

    @Test
    void testSearchWithFuzzyOnly() {
        Set<Integer> result = dictionaryService.search("ghjja");
        assertEquals(Set.of(2), result); // fuzzy match with ghjjk5
    }

    @Test
    void testSearchWithDifferentLength() {
        Set<Integer> result = dictionaryService.search("abcd");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchThrowsOnNull() {
        assertThrows(InvalidSearchQueryException.class, () -> dictionaryService.search(null));
    }

    @Test
    void testSearchThrowsOnEmptyString() {
        assertThrows(InvalidSearchQueryException.class, () -> dictionaryService.search("  "));
    }

    @Test
    void testSearchThrowsOnTooLongString() {
        String longWord = "a".repeat(256);
        assertThrows(InvalidSearchQueryException.class, () -> dictionaryService.search(longWord));
    }

    @Test
    void testUpdateDictionaryOverridesPreviousState() {
        dictionaryService.updateDictionary(List.of(
                new DictionaryRecord(99, "newword")
        ));
        Set<Integer> result = dictionaryService.search("newword");
        assertEquals(Set.of(99), result);

        Set<Integer> oldResult = dictionaryService.search("abc");
        assertTrue(oldResult.isEmpty());
    }
}
