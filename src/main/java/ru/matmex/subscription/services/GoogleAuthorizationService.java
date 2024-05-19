package ru.matmex.subscription.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;

/**
 * Сервис для работы с авторизаций в гугл аккаунт
 */
public interface GoogleAuthorizationService {
    /**
     * @param responseUrl - ссылка, после авторизации гугл аккаунта в браузере
     * @return авторизованные учетные данные клиента
     */
    Credential getCredentials(String responseUrl);

    /**
     * @return протокол передачи данных(HTTP|HTTPS)
     */
    NetHttpTransport getHttpTransport();

    /**
     * @return url для авторизации в гугл аккаунт
     */
    String getAuthorizationUrl();

    /**
     * @return учетные данные гугл аккаунта текущего пользователя
     */
    Credential getCurrentUserCredential();

    /**
     * @return экземпляр класса гугл календаря
     */
    Calendar getCalendar();

}
