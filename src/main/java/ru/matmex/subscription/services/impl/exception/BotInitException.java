package ru.matmex.subscription.services.impl.exception;

public class BotInitException extends RuntimeException {
    public BotInitException(String errorMessage) {
        super(errorMessage);
    }
}
