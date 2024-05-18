package ru.matmex.subscription.models.category;

/**
 * Модель создания категории. Клиент на своей стороне заполняет только имя, оставшийся процесс создания на сервере.(DTO)
 *
 * @param name - имя категории
 */
public record CreateCategoryModel(String name) {
};
