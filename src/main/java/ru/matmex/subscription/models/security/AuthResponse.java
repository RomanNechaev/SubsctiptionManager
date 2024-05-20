package ru.matmex.subscription.models.security;

/**
 * В случаи успеха аутенфикации, пользователь получает ответ с jwt, который затем использует для авторизации.
 *
 * @param username - имя пользователя
 * @param jwtToken
 */
public record AuthResponse(String username, String jwtToken) {
}
