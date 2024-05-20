package ru.matmex.subscription.services.notifications;

import java.util.Date;

/**
 * Уведомление, полученное от различных сервисов.
 */
public class Notification {

    private final String message;
    private final Date date;
    private final Long userId;

    public Notification(String message, Long userId) {
        this.message = message;
        this.userId = userId;
        this.date = new Date();
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public Long getUserId() {
        return userId;
    }

}
