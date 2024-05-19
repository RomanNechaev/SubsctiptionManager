package ru.matmex.subscription.services.impl.exception;

public class CryptoException extends RuntimeException {
    public CryptoException(String errorMessage) {
        super(errorMessage);
    }
}
