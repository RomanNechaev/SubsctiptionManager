package ru.matmex.subscription.services.impl.exception;

public class CalendarException extends RuntimeException {
    public CalendarException(String errorMessage) {
        super(errorMessage);
    }
}
