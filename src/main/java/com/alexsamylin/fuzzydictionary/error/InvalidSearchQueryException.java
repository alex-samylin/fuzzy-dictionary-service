package com.alexsamylin.fuzzydictionary.error;

public class InvalidSearchQueryException extends RuntimeException {

    public InvalidSearchQueryException(String message) {
        super(message);
    }

}
