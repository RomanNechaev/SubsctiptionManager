package ru.matmex.subscription.models.category;

import ru.matmex.subscription.entities.Subscription;

import java.util.List;

/**
 * Модель категории. Нужна, чтобы отдавать клиенту только часть данных о сущности категории.(DTO)
 *
 * @param id            - идентификатор категории в БД
 * @param name          - имя категории
 * @param subscriptions - список подписок в категории
 */
public record CategoryModel(Long id, String name, List<Subscription> subscriptions) {
}