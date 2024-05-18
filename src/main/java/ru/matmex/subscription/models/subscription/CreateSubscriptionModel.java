package ru.matmex.subscription.models.subscription;

/**
 * Модель создания подписки. Клиент на своей стороне заполняет входные данные. Остальное на сервере.
 *
 * @param name        - имя подписки
 * @param paymentDate - дата платежа
 * @param price       - цена
 * @param category    - категория
 */
public record CreateSubscriptionModel(String name, String paymentDate, Double price, String category) {
}
