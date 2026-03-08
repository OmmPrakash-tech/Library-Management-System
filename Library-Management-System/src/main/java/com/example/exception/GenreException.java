package com.example.exception;

public class GenreException extends RuntimeException {

    public GenreException(String message) {
        super(message);
    }

    public GenreException(String message, Throwable cause) {
        super(message, cause);
    }
}