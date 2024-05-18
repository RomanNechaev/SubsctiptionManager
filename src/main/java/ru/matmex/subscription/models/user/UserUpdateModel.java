package ru.matmex.subscription.models.user;

/**
 * Модель обновления пользователя. Клиент, зная индетефикатор пользователя может обноваить доступные ему данные.
 *
 * @param id    - идентификатор пользователя в БД
 * @param name  - новое имя пользователя
 * @param email - новый email
 */
public record UserUpdateModel(Long id, String name, String email) {
}
