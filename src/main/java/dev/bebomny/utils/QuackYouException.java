package dev.bebomny.utils;

public class QuackYouException extends RuntimeException{

    //Use when there is an edge case somewhere that shouldn't break anything, but it does anyway hah
    public QuackYouException(String message) {
        super(message);
    }

    public QuackYouException(String message, Throwable cause) {
        super(message, cause);
    }
}
