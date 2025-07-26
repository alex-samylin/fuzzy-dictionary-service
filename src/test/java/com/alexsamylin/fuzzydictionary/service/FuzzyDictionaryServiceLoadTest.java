package com.alexsamylin.fuzzydictionary.service;

import com.alexsamylin.fuzzydictionary.model.DictionaryRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

class FuzzyDictionaryServiceLoadTest {

    private FuzzyDictionaryService dictionaryService;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        dictionaryService = new FuzzyDictionaryService(new FuzzyMatcher());
        dictionaryService.setMaxQueryLength(255);

        List<DictionaryRecord> records = new ArrayList<>();

        for (int i = 1; i <= 50_000; i++) {
            String word1 = generateRandomWord(5 + random.nextInt(3));
            String word2 = generateRandomWord(5 + random.nextInt(3));
            String word3 = generateRandomWord(5 + random.nextInt(3));
            String text = word1 + " " + word2 + " " + word3;

            records.add(new DictionaryRecord(i, text));
        }

        dictionaryService.updateDictionary(records);
    }

    private String generateRandomWord(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(5)));
        }
        return sb.toString();
    }

    @Test
    void searchShouldBeFastUnderConcurrentLoad() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Callable<Set<Integer>>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            String query = generateRandomWord(5 + random.nextInt(3));
            tasks.add(() -> dictionaryService.search(query));
        }

        long start = System.nanoTime();
        executor.invokeAll(tasks);
        long end = System.nanoTime();

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(end - start);
        double qps = (threadCount * 1000.0) / elapsedMillis;

        System.out.println("Completed " + threadCount + " parallel searches in " + elapsedMillis + " ms");
        System.out.printf("QPS (Queries Per Second): %.2f%n", qps);

        executor.shutdown();
    }

}
