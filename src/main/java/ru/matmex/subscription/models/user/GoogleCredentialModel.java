package ru.matmex.subscription.models.user;

public record GoogleCredentialModel(String accessToken, Long expirationTimeMilliseconds, String refreshToken) {
}
