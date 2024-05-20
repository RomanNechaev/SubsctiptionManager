package ru.matmex.subscription.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.matmex.subscription.helpers.ErrorMessage;

/**
 * Перехватывает и обрабатывает ошибоки, которые могут возникуть в приложении
 */
@RestControllerAdvice
public class ExceptionApiHandler {
    /**
     * @param exception сущность в бд не найдена
     * @return Возвращает сообщение о том, что сущность в БД не найдена
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(exception.getMessage()));
    }

    /**
     * @param exception Имя пользователя не найдено
     * @return Возвращает сообщение о том, что имя пользователя не найдено
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> usernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(exception.getMessage()));
    }

    /**
     * @param exception Недопустимое состояние аргументов
     * @return Возвращает сообщение о том, пользователь передал недопустимое состояние аргументов
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(exception.getMessage()));
    }

    /**
     * @param exception Сущность в бд уже существует
     * @return Возвращает сообщение о том, сущность в БД уже существует
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorMessage> entityExistsException(EntityExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(exception.getMessage()));
    }

    /**
     * @param exception ошибка связнная с аутентификацией
     * @return сообщание о том, что произошла ошибка аутентификации
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> authenticationServiceException(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED).body(new ErrorMessage(exception.getMessage()));
    }
}
