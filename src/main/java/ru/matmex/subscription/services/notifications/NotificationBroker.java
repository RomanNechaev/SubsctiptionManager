package ru.matmex.subscription.services.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Брокер который управляет уведомлениями и отправляет их нужным подписчикам
 */
public final class NotificationBroker {
    private static volatile NotificationBroker instance = null;
    private final Queue<Notification> notifications;
    private final List<NotificationSender> notificationSenderList;

    private NotificationBroker() {
        this.notifications = new ConcurrentLinkedQueue<>();
        this.notificationSenderList = new ArrayList<>();
    }

    public static NotificationBroker getInstance() {
        if (instance == null) {
            synchronized (NotificationBroker.class) {
                instance = new NotificationBroker();
            }
        }
        return instance;
    }

    /**
     * Добавить уведомление в очередь
     */
    public synchronized void addNotification(Notification notification) {
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
     * Отправить уведомление всем рассыльщикам
     */
    public synchronized void notifyAllSubscriber() {
        for (NotificationSender sender : notificationSenderList) {
            sender.sendNotification(getLastNotification());
        }
    }

    /**
     * Добавить рассыльщика уведомлений
     *
     * @param sender - рассыльщик уведомлений
     */
    private void addNotificationSender(NotificationSender sender) {
        notificationSenderList.add(sender);
    }

    /**
     * Удалить рассыльщика уведомлений
     *
     * @param sender - рассыльщик уведомлений
     */
    private void removeNotificationSender(NotificationSender sender) {
        notificationSenderList.remove(sender);
    }
}
