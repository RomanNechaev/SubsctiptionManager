package ru.matmex.subscription.models.category;

/**
 * Модель обновления категории. Клиент может обновить имя категории, зная её id.(DTO)
 *
 * @param id   - идентификатор категории в БД
 * @param name - новое имя категории
 */
public record UpdateCategoryModel(Long id, String name) {
}
