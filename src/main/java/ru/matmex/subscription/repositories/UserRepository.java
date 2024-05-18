package ru.matmex.subscription.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.matmex.subscription.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями подписки
 */
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Получить пользователя по имени
     *
     * @param username - имя пользователя
     * @return пользователей в виде обертки
     */
    Optional<User> findByUsername(String username);

    /**
     * Получить пользователя по идентификатору
     *
     * @param id - идентификатор пользователя в БД
     * @return пользователей в виде обертки
     */
    Optional<User> getById(Long id);

    /**
     * Проверить, существует ли пользователь в БД
     *
     * @param username - имя пользователя
     * @return
     */
    Boolean existsByUsername(String username);

    /**
     * Получить всех пользователей из БД
     *
     * @return
     */
    List<User> findAll();

}
