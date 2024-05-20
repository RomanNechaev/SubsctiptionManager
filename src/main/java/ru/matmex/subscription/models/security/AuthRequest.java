package ru.matmex.subscription.models.security;

/**
 * Запрос на аутентификацию в приложение
 *
 * @param username - имя пользователя
 * @param password - пароль
 */
public record AuthRequest(String username, String password) {
}
