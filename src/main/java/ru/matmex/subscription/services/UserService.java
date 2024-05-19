package ru.matmex.subscription.services;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.user.GoogleCredentialModel;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.models.user.UserUpdateModel;

import java.util.List;

/**
 * Сервис для работы с данными пользователей
 */
public interface UserService extends UserDetailsService {
    /**
     * Добавить пользователя
     *
     * @param userRegistrationModel - регистрационные данные пользователя
     * @return информация о зарегистрированном пользователе
     */
    UserModel adduser(UserRegistrationModel userRegistrationModel);

    /**
     * Обновить пользователя
     *
     * @param userUpdateModel - обновленная информация о пользователе
     * @return информация об обновленном пользователе
     */
    UserModel updateUser(UserUpdateModel userUpdateModel);

    /**
     * Получить модель пользователя по id
     *
     * @param userId - идентификатор пользователя
     * @return информация о пользователе
     */
    UserModel getUserModel(Long userId);

    /**
     * Получить модель пользователя по имени
     *
     * @param username - имя пользователя
     * @return информация о пользователе
     */
    UserModel getUser(String username);

    User getUser(Long id) throws UsernameNotFoundException;

    /**
     * Получить пользователя в текущей сессии
     *
     * @return сущность пользователя
     */
    User getCurrentUser();

    /**
     * Удалить пользователя
     *
     * @param username имя пользователя
     * @return информация об удалении
     * @throws jakarta.persistence.EntityNotFoundException
     */
    String delete(String username);

    /**
     * Получить всех пользователей
     *
     * @return список всех пользователей
     */
    List<UserModel> getUsers();

    void setTelegramChatId(User user, long telegramChatId);

    /**
     * Получить реквизиты для входа в гугл аккаунт текущего пользователя
     */
    GoogleCredentialModel getGoogleCredentialCurrentUser();

    /**
     * Получить реквизиты для входа в гугл аккаунт
     *
     * @param id - id пользователя
     */
    GoogleCredentialModel getGoogleCredential(Long id);

    /**
     * Присвоить текущему пользователю реквизиты для входа в гугл аккаунт
     *
     * @param credential - учетные данные гугл аккаунта полученные от гугл сервиса
     */
    void setGoogleCredential(Credential credential);

    /**
     * Узнать привязан ли гугл-аккаунт у пользователя 
     * @param id пользователя
     */
    boolean isGoogleAccountLinked(Long id);
}
