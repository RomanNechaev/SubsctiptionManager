package ru.matmex.subscription.models.subscription;

/**
 * Модель обноваления подписки. Клиент, зная индетефикатор подписки может обноваить доступные ему данные.
 *
 * @param id          - идентификатор подписки в БД
 * @param name        - имя подписки
 * @param paymentDate - дата платежа
 * @param price       - цена
 * @param category    - категория
 */
public record UpdateSubscriptionModel(Long id, String name, String paymentDate, Double price, String category) {
}
