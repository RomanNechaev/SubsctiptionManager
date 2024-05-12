package ru.matmex.subscription.services;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.user.GoogleCredentialModel;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.models.user.UserUpdateModel;

import java.util.List;

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
     * Получить пользователя по имени
     *
     * @param username - имя пользователя
     * @return информация о пользователе
     */
    UserModel getUser(String username);

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
     * Получить информацю о привязки гугл аккаунта
     */
    String getInformationAboutGoogle(Long id);
}
