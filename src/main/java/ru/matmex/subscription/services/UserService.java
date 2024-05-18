package ru.matmex.subscription.services;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.matmex.subscription.entities.GoogleCredential;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.models.user.UserUpdateModel;

import java.io.IOException;
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
    UserModel getUserModel(String username);

    User getUser(String username) throws UsernameNotFoundException;

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

    boolean checkIntegrationWithTelegram();

    void setTelegramChatId(User user, long telegramChatId);

    /**
     * Получить реквизиты для входа в гугл аккаунт текущего пользователя
     */
    GoogleCredential getGoogleCredential() throws IOException;

    /**
     * Получить реквизиты для входа в гугл аккаунт
     *
     * @param username - имя пользователя
     */
    GoogleCredential getGoogleCredential(String username) throws IOException;

    /**
     * Присвоить текущему пользователю реквизиты для входа в гугл аккаунт
     */
    void setGoogleCredential(Credential credential) throws IOException;

    /**
     * Получить информацю о привязки гугл аккаунта
     */
    String getInformationAboutGoogle();
}
