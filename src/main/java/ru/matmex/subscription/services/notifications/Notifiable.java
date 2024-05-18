package ru.matmex.subscription.services.notifications;

/**
 * Регистратор событий
 */
public abstract class Notifiable {
    private final NotificationBroker notificationBroker = NotificationBroker.getInstance();

    /**
     * Зарегистироваться уведомление
     *
     * @param message  - уведомление
     * @param username - имя пользователя
     */
    public void registerNotification(String message, String username) {
        notificationBroker.addNotification(new Notification(message, username));
        notificationBroker.notifyAllSubscriber();
    }

    /**
     * Добавить рассыльщика
     */
    public void addNotificationSender(NotificationSender sender) {
        notificationBroker.addNotificationSender(sender);
    }

}
