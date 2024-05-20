package ru.matmex.subscription.services.utils.mapping;

import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.user.UserModel;

/**
 * Преобразование сущности пользователя в модель пользователя
 */
public class UserModelMapper {
    private final CategoryModelMapper categoryModelMapper;

    public UserModelMapper(CategoryModelMapper categoryModelMapper) {
        this.categoryModelMapper = categoryModelMapper;
    }

    public UserModel map(User user) {
        return new UserModel(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getTelegramChatId(),
                user.getCategories()
                        .stream()
                        .map(categoryModelMapper::map).toList());
    }
}
