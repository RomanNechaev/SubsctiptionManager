package ru.matmex.subscription.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.matmex.subscription.models.user.UserModel;
import ru.matmex.subscription.models.user.UserUpdateModel;
import ru.matmex.subscription.services.UserService;

import java.util.List;

/**
 * Контроллер для операций с пользователем
 */
@Controller
public class UserController {
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обновить пользовательские данные
     *
     * @param userUpdateModel - данные для обноваления пользвателя
     * @return HTTP ответ с информацией об обноленном пользователи
     */
    @PostMapping(value = "/api/app/user")
    public ResponseEntity<UserModel> update(@RequestBody UserUpdateModel userUpdateModel) {
        return ResponseEntity.ok(userService.updateUser(userUpdateModel));
    }

    /**
     * Получить информацию о всех пользователей
     *
     * @return HTTP ответ с информацией о всех пользователях
     */
    @GetMapping(value = "/api/admin/app/users")
    public ResponseEntity<List<UserModel>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Получить информацию о пользователе по имени
     *
     * @param username имя пользователя
     * @return HTTP ответ с информаицей о пользователе
     */
    @GetMapping(value = "/api/admin/app/{username}")
    public ResponseEntity<UserModel> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    /**
     * Удалить пользователя
     *
     * @param username имя пользователя
     * @return HTTP ответ с информаиией об успешном удалении
     */
    @DeleteMapping(value = "/api/admin/app/{username}")
    public ResponseEntity<String> delete(@PathVariable String username) {
        return ResponseEntity.ok(userService.delete(username));
    }
}
