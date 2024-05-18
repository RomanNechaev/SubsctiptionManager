package ru.matmex.subscription.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserRegistrationModel;
import ru.matmex.subscription.services.CategoryService;
import ru.matmex.subscription.services.UserService;

/**
 * Контроллер для регистрации пользователя
 */
@Controller
@CrossOrigin
public class RegistrationController {
    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public RegistrationController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    /**
     * Регистрация пользователя в приложении
     *
     * @param userRegistrationModel - данные о пользователе
     * @return HTTP ответ с данными о пользователе
     */
    @PostMapping("/registration")
    public ResponseEntity<UserModel> registration(@RequestBody UserRegistrationModel userRegistrationModel) {
        UserModel user = userService.adduser(userRegistrationModel);
        categoryService.createDefaultSubscription(userService.getUser(user.username()));
        return ResponseEntity.ok(user);
    }
}
