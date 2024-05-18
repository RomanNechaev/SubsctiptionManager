package ru.matmex.subscription.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями категории
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {
    /**
     * Получить категорию по имени и пользоватю
     *
     * @param name - имя категории
     * @param user - пользователь
     * @return категория в виде обертки
     */
    Optional<Category> findCategoryByNameAndUser(String name, User user);

    /**
     * Получить категории пользователя
     *
     * @param user - пользователь
     * @return список категорий в виде обертки
     */
    List<Category> findCategoriesByUser(User user);

    /**
     * Получить категории по id
     *
     * @param id - идентификатор категории в БД
     * @return категория в виде обертки
     */
    Optional<Category> findCategoryById(Long id);

    /**
     * Проверить, существует ли категория в БД
     *
     * @param name - имя категории
     * @param user - пользователь
     * @return
     */
    boolean existsByNameAndUser(String name, User user);
}
