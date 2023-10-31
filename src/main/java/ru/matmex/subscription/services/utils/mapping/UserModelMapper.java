package ru.matmex.subscription.services.utils.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
                user.getCategories()
                        .stream()
                        .map(categoryModelMapper::map).toList());
    }
}
