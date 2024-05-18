package ru.matmex.subscription.services.notifications;

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
    private final List<NotificationSender> notificationSenderList;

    public NotificationService() {
        this.notifications = new ConcurrentLinkedQueue<>();
        this.notificationSenderList = new ArrayList<>();
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
        for (NotificationSender sender : notificationSenderList) {
            sender.sendNotification(lastNotification);
        }
    }

    /**
     * Добавить рассыльщика уведомлений
     *
     * @param sender - рассыльщик уведомлений
     */
    public void addNotificationSender(NotificationSender sender) {
        notificationSenderList.add(sender);
    }

    /**
     * Удалить рассыльщика уведомлений
     *
     * @param sender - рассыльщик уведомлений
     */
    public void removeNotificationSender(NotificationSender sender) {
        notificationSenderList.remove(sender);
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
