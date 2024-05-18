package ru.matmex.subscription.models.user;

import ru.matmex.subscription.models.category.CategoryModel;

import java.util.List;

/**
 * Модель пользователя. Хранит только доступные данные для клиента.
 *
 * @param id         - идентификатор пользователя в БД
 * @param username   - имя пользователя
 * @param categories - список категорий
 */
public record UserModel(Long id, String username, String email, Long tgId,
                        List<CategoryModel> categories) {
}
