package ru.matmex.subscription.services;

import jakarta.persistence.EntityNotFoundException;
import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.category.CategoryModel;
import ru.matmex.subscription.models.category.CreateCategoryModel;
import ru.matmex.subscription.models.category.UpdateCategoryModel;

import java.util.List;

public interface CategoryService {

    /**
     * Получить категорию текущего юзера
     *
     * @return Список категорий текущего пользователя
     */
    List<CategoryModel> getCategoriesByCurrentUsername();

    /**
     * Получить категорию по имени
     *
     * @param name Имя категории
     * @return категория с именем name
     */
    Category getCategory(String name);

    /**
     * Преобразовать сущность категории в модель категории
     *
     * @param name имя категори
     * @return модель категории
     */
    CategoryModel getCategoryToClient(String name);

    /**
     * Создать категорию
     *
     * @param createCategoryModel - данные, заполненные пользователем при создании категории на клиенте
     * @return модель категории
     */
    CategoryModel create(CreateCategoryModel createCategoryModel);

    /**
     * Создать категорию по умолчанию
     *
     * @param user - Сущность юзера
     */
    void createDefaultSubscription(User user);

    /**
     * Обновить категорию
     *
     * @param updateCategoryModel параметры, заполненные пользователем на клиенте
     * @return модель категории
     */
    CategoryModel update(UpdateCategoryModel updateCategoryModel);

    /**
     * Удалить категорию
     *
     * @param id - индетификатор категории в БД
     * @return Сообщение об успешном удалении, в случае если не произошла ошибка
     * @throws EntityNotFoundException - ошибка о том, что категория не найдена
     */
    String delete(Long id);
}
