package com.alexsamylin.fuzzydictionary.controller;

import com.alexsamylin.fuzzydictionary.service.FuzzyDictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dictionary")
@Slf4j
@Tag(name = "Fuzzy Dictionary", description = "API for fuzzy word search in dictionary")
public class FuzzySearchController {

    private final FuzzyDictionaryService fuzzyDictionaryService;

    @Operation(
            summary = "Search for matching record IDs",
            description = "Returns a list of record IDs where the given word appears exactly or with one character difference"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter: word must be non-empty and up to 255 characters")
    })
    @GetMapping("/search")
    public List<Integer> search(@Parameter(description = "Word to search in the dictionary (only Latin letters are considered)") @RequestParam String word) {
        Set<Integer> resultIds = fuzzyDictionaryService.search(word);
        return resultIds.stream().sorted().toList();
    }
}
