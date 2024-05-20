package ru.matmex.subscription.services.impl.security;

import org.springframework.security.core.Authentication;

/**
 * Хранит текущее состояния авторизации пользователя
 */
public class AuthenticationContext {
    private static Authentication authenticationContext;

    /**
     * Получить авторизационные данные
     *
     * @return авторизационные куки
     */
    public static synchronized Authentication getAuthenticationContext() {
        return authenticationContext;
    }

    /**
     * Установить авторизацию
     *
     * @param authenticationContext авторизацинные данные
     */
    public static synchronized void setAuthenticationContext(Authentication authenticationContext) {
        AuthenticationContext.authenticationContext = authenticationContext;
    }
}
