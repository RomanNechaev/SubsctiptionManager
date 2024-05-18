package ru.matmex.subscription.controllers;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.matmex.subscription.services.GoogleCalendarService;
import ru.matmex.subscription.services.GoogleAuthorizationService;
import ru.matmex.subscription.services.SubscriptionService;
import ru.matmex.subscription.services.UserService;

/**
 * Контроллер для работы с гугл-сервисами: гугл-календарь и гугл-диск
 */
@Controller
public class GoogleServicesController {
    private final GoogleCalendarService googleCalendarService;
    private final UserService userService;
    private final GoogleAuthorizationService googleAuthorizationService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public GoogleServicesController(GoogleCalendarService googleCalendarService, UserService userService, GoogleAuthorizationService googleAuthorizationService, SubscriptionService subscriptionService) {
        this.googleCalendarService = googleCalendarService;
        this.userService = userService;
        this.googleAuthorizationService = googleAuthorizationService;
        this.subscriptionService = subscriptionService;
    }

    /**
     * Узнать информацию о подключении к гугл аккаунту
     *
     * @return HTTP ответ с информаицей
     */
    @GetMapping(value = "/api/app/google")
    public ResponseEntity<String> IsLinkedGoogleAccount() {
        return ResponseEntity.ok(userService.getInformationAboutGoogle(userService.getCurrentUser().getId())
                ? "гугл аккаунт не привязан" : "гугл аккаунт успешно привязан");
    }

    /**
     * Получить сслыку для авторизации гугл аккаунта
     *
     * @return сслыка для авторизации гугл аккаунта
     * */
    @GetMapping(value = "/api/app/google/link")
    public ResponseEntity<String> linkGoogleAccount() {
        return ResponseEntity.ok(googleAuthorizationService.getAuthorizationUrl());
    }

    /**
     * Авторизовать гугл аккаунт
     *
     * @param authorizationLink ссылка в браузере после авторизации в гугл аккаунт
     * @return информация об авторизации
     */
    @PostMapping("/api/app/google/authorize")
    public ResponseEntity<Credential> authorizeToGoogleAccount(@RequestBody String authorizationLink) {
        return ResponseEntity.ok(googleAuthorizationService.getCredentials(authorizationLink));
    }

    /**
     * Перенос подпискок пользователя и их сроков действия в гугл-календарь
     */
    @RequestMapping(value = "/api/app/google/calendar")
    public ResponseEntity<String> copySubscriptionsToGoogleCalendar() {
        googleCalendarService.copySubscriptionsToCalendar(googleAuthorizationService.getCalendar(),
                subscriptionService.getSubscriptions());
        return new ResponseEntity<>("Перенос подписок успешно совершен", HttpStatus.OK);
    }

}
