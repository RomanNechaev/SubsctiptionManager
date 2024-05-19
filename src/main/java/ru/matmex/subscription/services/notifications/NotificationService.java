package ru.matmex.subscription.services.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Сервис для управление уведомлениями и отправки подписчикам
 */
@Service
public class NotificationService {
    private final Queue<Notification> notifications;
    private final NotificationSenderManager notificationSenderManager;

    @Autowired
    public NotificationService(NotificationSenderManager notificationSenderManager) {
        this.notificationSenderManager = notificationSenderManager;
        this.notifications = new ConcurrentLinkedQueue<>();
    }

    /**
     * Добавить уведомление в очередь
     */
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    /**
     * Получить последнее уведомление
     *
     * @return уведомление
     */
    private Notification getLastNotification() {
        return notifications.poll();
    }

    /**
     * Уведомить всех рассыльщиков
     */
    public void notifyAllSubscriber() {
        Notification lastNotification = getLastNotification();
        for (NotificationSender sender : notificationSenderManager.getNotificationSenderList()) {
            sender.sendNotification(lastNotification);
        }
    }

    /**
     * Зарегистировать уведомление
     *
     * @param message  - уведомление
     * @param userId - идентификатор пользователя
     */
    public void registerNotification(String message, Long userId) {
        addNotification(new Notification(message, userId));
        notifyAllSubscriber();
    }
}
