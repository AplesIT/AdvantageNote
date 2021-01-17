package com.production.advangenote.exception;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String message, Exception e) {
        super(message, e);

    }
}
