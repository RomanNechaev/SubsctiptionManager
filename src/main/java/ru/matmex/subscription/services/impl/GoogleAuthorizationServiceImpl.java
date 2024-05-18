package ru.matmex.subscription.services.impl;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.SubscriptionApplication;
import ru.matmex.subscription.models.security.GoogleCredentialHandler;
import ru.matmex.subscription.services.GoogleAuthorizationService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.impl.exception.GoogleAuthorizationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с авторизаций в гугл аккаунт
 */
@Service
public class GoogleAuthorizationServiceImpl implements GoogleAuthorizationService {
    private final UserService userService;

    /**
     * Глобальный экземпляр фабрики JSON.
     */
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Глобальный экземпляр областей, необходимых для  API  гугл календаря
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    /**
     * Учетные данные от приложения
     */
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    /**
     * Управление процессом авторизации
     */
    private GoogleAuthorizationCodeFlow flow;
    /**
     * Локальный сервер, использующийся для процесса авторизации по протоколу OAuth 2.0.
     */
    private LocalServerReceiver receiver;

    @Autowired
    public GoogleAuthorizationServiceImpl(UserService userService) throws GeneralSecurityException, IOException {
        this.userService = userService;
    }

    @PostConstruct
    private void init() {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                getHttpTransport(), JSON_FACTORY, loadClientSecrets(), SCOPES)
                .setAccessType("offline")
                .build();
        receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    }


    @Override
    public Credential getCredentials(String responseUrl) {
        return new GoogleCredentialHandler(flow, receiver, userService).authorize(responseUrl);
    }

    @Override
    public String getAuthorizationUrl() {
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(getRedirectUri());
        return authorizationUrl.build();
    }

    /**
     * @return URI перенаправления
     */
    private String getRedirectUri() {
        try {
            return receiver.getRedirectUri();
        } catch (IOException e) {
            throw new GoogleAuthorizationException("Не удалось получить RedirectUri" + e.getMessage());
        }
    }

    @Override
    public Credential getCurrentUserCredential() {
        return new GoogleCredentialHandler(flow, receiver, userService).loadCredential(userService.getGoogleCredentialCurrentUser());
    }

    public NetHttpTransport getHttpTransport() {
        return HTTP_TRANSPORT;
    }

    /**
     * @return Секретные данные клиента необходимые для авторизации
     */
    private GoogleClientSecrets loadClientSecrets() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream(CREDENTIALS_FILE_PATH)));
            return GoogleClientSecrets.load(JSON_FACTORY, inputStreamReader);
        } catch (IOException e) {
            throw new GoogleAuthorizationException("Не удалось загрузить GoogleClientSecrets" + e.getMessage());
        }
    }

    /**
     * Получение гугл-календаря пользователя
     */
    @Override
    public Calendar getCalendar() {
        return new Calendar.Builder(getHttpTransport(),
                JSON_FACTORY,
                getCurrentUserCredential())
                .setApplicationName(SubscriptionApplication.class.getName())
                .build();

    }
}
