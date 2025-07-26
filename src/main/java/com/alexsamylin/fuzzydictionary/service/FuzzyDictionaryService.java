package com.alexsamylin.fuzzydictionary.service;

import com.alexsamylin.fuzzydictionary.error.InvalidSearchQueryException;
import com.alexsamylin.fuzzydictionary.model.DictionaryRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuzzyDictionaryService {

    private volatile Map<Integer, Map<String, Set<Integer>>> wordIndexByLength = new HashMap<>();

    private final FuzzyMatcher fuzzyMatcher;

    @Value("${fuzzy-dictionary.search.max-query-length:255}")
    private int maxQueryLength;

    public synchronized void updateDictionary(List<DictionaryRecord> entries) {
        log.info("Starting dictionary index update...");
        Map<Integer, Map<String, Set<Integer>>> newIndex = new HashMap<>();

        for (DictionaryRecord entry : entries) {
            String[] words = entry.getText().split("\\s+");
            for (String word : words) {
                String normalizedWord = fuzzyMatcher.normalize(word);
                if (!normalizedWord.isEmpty()) {
                    int length = normalizedWord.length();
                    newIndex
                            .computeIfAbsent(length, l -> new HashMap<>())
                            .computeIfAbsent(normalizedWord, w -> new HashSet<>())
                            .add(entry.getId());
                }
            }
        }

        this.wordIndexByLength = newIndex;
        log.info("Dictionary index updated. Word lengths indexed: {}", wordIndexByLength.keySet());
    }

    public Set<Integer> search(String queryWord) {
        if (queryWord == null || queryWord.isBlank()) {
            throw new InvalidSearchQueryException("Search query must not be null or empty.");
        }

        if (queryWord.length() > maxQueryLength) {
            throw new InvalidSearchQueryException("Search query exceeds maximum allowed length of " + maxQueryLength + " characters.");
        }

        String normalizedQuery = fuzzyMatcher.normalize(queryWord);

        if (normalizedQuery.isEmpty()) {
            log.warn("Search skipped: input word is empty after normalization.");
            return Collections.emptySet();
        }

        int queryLength = normalizedQuery.length();
        Map<String, Set<Integer>> candidates = wordIndexByLength.get(queryLength);

        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Integer> resultIds = new HashSet<>();

        // Fuzzy matches
        candidates.entrySet()
                .stream()
                .filter(entry -> fuzzyMatcher.isFuzzyMatch(normalizedQuery, entry.getKey()))
                .forEach(entry -> {
                    resultIds.addAll(entry.getValue());
                    log.debug("Match: '{}' ~ '{}' → IDs: {}", normalizedQuery, entry.getKey(), entry.getValue());
                });

        log.info("Search completed for '{}'. Total matching IDs: {}", normalizedQuery, resultIds.size());
        return resultIds;
    }

    void setMaxQueryLength(int maxQueryLength) {
        this.maxQueryLength = maxQueryLength;
    }
}
