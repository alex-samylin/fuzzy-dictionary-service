# Fuzzy Dictionary Service

**Fuzzy Dictionary** is a service that implements fuzzy word search based on a preloaded dictionary from a CSV file. The project is built with Spring Boot and provides fast access to records by exact or approximate word matches.

## Core Functionality

### Dictionary Loading

- **Source**: CSV file (`ID,TEXT` format).
- **Limit**: Up to **50,000** rows.
- Dictionary is initialized:
  - at application startup;
  - and automatically refreshed every hour (via scheduled task).

### Word Search

HTTP endpoint:

```
GET /api/v1/dictionary/search?word=...
```

- Parameter `word` is the search query.
- The response is a list of `ID`s for records that contain the word:
  - either as an **exact match** (after normalization),
  - or with **one character difference** (same length, one mismatch allowed).

---

## Search Optimization

The implementation is optimized based on project constraints:

- up to **50,000** records;
- maximum word length: **255** characters.

This allows the use of a simple and efficient hash-based structure, without relying on external libraries or complex algorithms.

### Storage Structure

The dictionary is stored in:

```java
Map<Integer, Map<String, Set<Integer>>>
```

- **Outer Key**: word length (int);
- **Inner Key**: normalized word (Latin letters only, lowercase);
- **Value**: set of IDs where this word appears;

This structure allows fast lookup and length-based filtering, which narrows down the fuzzy search space significantly.

### Search Algorithm

The search is performed in a single stage:

- The input word is normalized (non-Latin characters removed, lowercased).
- The dictionary group corresponding to the word's length is retrieved.
- Each word in that group is compared to the query using character-by-character fuzzy match.
- A match is considered valid if the number of different characters is **zero or one**.
- All matching entries are collected and returned as a set of associated record IDs.

This approach provides sufficient performance for datasets up to 50,000 entries.

---

## Testing

Implemented tests include:

- Unit tests:
  - word normalization,
  - exact and fuzzy match search,
  - validation of input data;
- Load testing:
  - generation of 50,000 dictionary entries,
  - 50 parallel search requests,
  - performance metrics (execution time and QPS) printed to console.

---

## Swagger Documentation

The project integrates `springdoc-openapi` for automatic API documentation:

```
http://localhost:8080/swagger-ui/index.html
```