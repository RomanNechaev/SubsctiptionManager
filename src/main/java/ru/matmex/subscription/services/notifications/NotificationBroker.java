package ru.matmex.subscription.services.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Брокер который управляет уведомлениями и отправляет их нужным подписчикам
 */
public class NotificationBroker {
    private static volatile NotificationBroker instance = null;
    private final Queue<Notification> notifications;
    private final List<NotificationSender> notificationSenderList;

    private NotificationBroker() {
        this.notifications = new ConcurrentLinkedQueue<>();
        this.notificationSenderList = new ArrayList<>();
    }

    /**
     * Получить экземляр объекта
     * <p>Класс {@link NotificationBroker} реализует паттерн Singleton</p>
     *
     * @return экземпляр класса
     */
    public static NotificationBroker getInstance() {
        if (instance == null) {
            synchronized (NotificationBroker.class) {
                if (instance == null) {
                    instance = new NotificationBroker();
                }
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
    private Notification getNotification() {
        return notifications.poll();
    }

    /**
     * Уведомить всех рассыльщиков
     */
    public synchronized void notifyAllSubscriber() {
        Notification lastNotification = getNotification();
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
}
