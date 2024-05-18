package ru.matmex.subscription.models.subscription;

import java.util.Date;

/**
 * Модель подписки. Хранит только часть данных, доступных пользователю. DTO
 *
 * @param id          - идентификатор подписки в БД
 * @param name        - имя подписки
 * @param paymentDate - дата платежа
 * @param price       - цена
 * @param category    - категория
 */
public record SubscriptionModel(
        Long id,
        String name,
        Date paymentDate,
        Double price,
        String category) {
}
