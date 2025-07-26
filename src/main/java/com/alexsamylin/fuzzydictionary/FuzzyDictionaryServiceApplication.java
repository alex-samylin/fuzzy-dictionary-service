package com.alexsamylin.fuzzydictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuzzyDictionaryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuzzyDictionaryServiceApplication.class, args);
	}

}
