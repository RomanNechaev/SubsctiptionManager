package ru.matmex.subscription.models.security;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import ru.matmex.subscription.models.user.GoogleCredentialModel;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.impl.exception.GoogleAuthorizationException;

import java.io.IOException;
import java.util.Objects;

/**
 * Обработчик учетных данных от гугл аккаунта
 */
public class GoogleCredentialHandler {
    private final AuthorizationCodeFlow flow;
    private final VerificationCodeReceiver receiver;
    private final UserService userService;

    public GoogleCredentialHandler(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver, UserService userService) {
        this.flow = Objects.requireNonNull(flow);
        this.receiver = Objects.requireNonNull(receiver);
        this.userService = userService;
    }

    /**
     * Авторизовать учетные данные от гугл аккаунта
     *
     * @param responseUrl - ссылка, после авторизации гугл аккаунта в браузере
     * @return авторизованные учетные данные
     */
    public Credential authorize(String responseUrl) {
        Credential newCredential;
        try {
            GoogleCredentialModel credential = userService.getGoogleCredentialCurrentUser();
            if (credential != null && (credential.refreshToken() != null)) {
                return loadCredential(credential);
            }
            AuthorizationCodeResponseUrl codeResponseUrl = new AuthorizationCodeResponseUrl(responseUrl);
            TokenResponse response = flow.newTokenRequest(codeResponseUrl.getCode())
                    .setRedirectUri(receiver.getRedirectUri())
                    .execute();
            newCredential = newCredential();
            newCredential.setFromTokenResponse(response);
            userService.setGoogleCredential(newCredential);
            receiver.stop();
        } catch (IOException e) {
            throw new GoogleAuthorizationException("Не удалось авторизоваться "+ e.getMessage());
        }
        return newCredential;
    }

    /**
     * Преобразовать учетные данные к валидному состоянию
     *
     * @param credential - учетные данные из БД
     * @return валидные учетные данные пользователя
     */
    public Credential loadCredential(GoogleCredentialModel credential) {
        Credential validCredential = newCredential();
        validCredential.setAccessToken(credential.accessToken());
        validCredential.setRefreshToken(credential.refreshToken());
        validCredential.setExpirationTimeMilliseconds(credential.expirationTimeMilliseconds());
        return validCredential;
    }

    /**
     * Создать новые учетные данные
     *
     * @return учетные данные пользователя
     */
    private Credential newCredential() {
        Credential.Builder builder = new Credential.Builder(flow.getMethod())
                .setTransport(flow.getTransport())
                .setJsonFactory(flow.getJsonFactory())
                .setTokenServerEncodedUrl(flow.getTokenServerEncodedUrl())
                .setClientAuthentication(flow.getClientAuthentication())
                .setRequestInitializer(flow.getRequestInitializer())
                .setClock(flow.getClock());
        return builder.build();
    }
}

