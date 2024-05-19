package ru.matmex.subscription.services.impl.exception;

public class GoogleAuthorizationException extends RuntimeException {
    public GoogleAuthorizationException(String errorMessage) {
        super(errorMessage);
    }
}
