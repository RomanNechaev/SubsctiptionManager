package ru.matmex.subscription.services.notifications;

import java.util.Date;

/**
 * Уведомление, полученное от различных сервисов.
 */
public class Notification {

    private final String message;
    private final Date currentDate;
    private final String username;

    public Notification(String message, String username) {
        this.message = message;
        this.username = username;
        this.currentDate = new Date();
    }

    public String getMessage() {
        return message;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public String getUsername() {
        return username;
    }

}
