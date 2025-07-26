package com.alexsamylin.fuzzydictionary.service;


import com.alexsamylin.fuzzydictionary.model.DictionaryRecord;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvDictionaryLoader {

    @Value("${app.csv.file-path}")
    private String csvFilePath;

    private final FuzzyDictionaryService fuzzyDictionary;

    @Scheduled(fixedRateString = "${app.dictionary.refresh-rate-ms}")
    public List<DictionaryRecord> loadAndRefreshData() {
        log.info("Starting CSV dictionary load from path: {}", csvFilePath);
        List<DictionaryRecord> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new ClassPathResource(csvFilePath).getInputStream()))) {

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                DictionaryRecord record = parseLineToRecord(line);
                entries.add(record);
            }

            fuzzyDictionary.updateDictionary(entries);
            log.info("Loaded {} records from CSV", entries.size());

            return entries;
        } catch (IOException e) {
            log.error("Error while loading CSV file: {}", e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    private DictionaryRecord parseLineToRecord(String line) {
        String[] parts = line.split(",", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed line: " + line);
        }

        int id = Integer.parseInt(parts[0].trim());
        String text = parts[1].trim();

        DictionaryRecord record = new DictionaryRecord();
        record.setId(id);
        record.setText(text);
        return record;
    }


    @PostConstruct
    public void init() {
        loadAndRefreshData();
    }
}
