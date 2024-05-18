package ru.matmex.subscription.services.notifications;

/**
 * Рассыльщик уведомлений
 */
public interface NotificationSender {
    /**
     * Отправить уведомление
     *
     * @param notification - уведомление
     */
    void sendNotification(Notification notification);
}
