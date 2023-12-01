package dev.bebomny.utils;

public class QuackYouException extends RuntimeException{

    public QuackYouException(String message) {
        super(message);
    }

    public QuackYouException(String message, Throwable cause) {
        super(message, cause);
    }
}
