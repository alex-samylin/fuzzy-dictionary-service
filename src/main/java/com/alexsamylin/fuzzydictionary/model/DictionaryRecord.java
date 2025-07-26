package com.alexsamylin.fuzzydictionary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DictionaryRecord {
    private int id;
    private String text;
}
