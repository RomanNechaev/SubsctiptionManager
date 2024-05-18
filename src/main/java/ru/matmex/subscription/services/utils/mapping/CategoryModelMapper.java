package ru.matmex.subscription.services.utils.mapping;

import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.models.category.CategoryModel;

import java.util.Objects;

/**
 * Преобразование сущности категории в модель категории
 */
public class CategoryModelMapper {

    public CategoryModel map(Category category) {
        return new CategoryModel(category.getId(),
                category.getName(),
                category.getSubscriptions()
                        .stream()
                        .filter(x -> Objects.equals(x.getUser().getId(), category.getUser().getId()))
                        .toList());
    }
}
