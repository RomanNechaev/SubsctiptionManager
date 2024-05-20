package ru.matmex.subscription.models.user;

/**
 * Модель создания пользователя. Клиент на своей стороне заполняет входные данные. Остальное на сервере.
 *
 * @param username - имя пользоватля
 * @param password - пароль
 * @param email
 */
public record UserRegistrationModel(String username, String password, String email) {
}
