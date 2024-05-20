package ru.matmex.subscription.services;

import ru.matmex.subscription.models.security.AuthRequest;
import ru.matmex.subscription.models.security.AuthResponse;

public interface AuthenticationService {
    /**
     * Авторизовать пользователя
     *
     * @param authRequest авторазационные данные пользователя
     * @return ответ, содержащий необходимую авторизационную информацию
     */
    AuthResponse authUser(AuthRequest authRequest);
}
