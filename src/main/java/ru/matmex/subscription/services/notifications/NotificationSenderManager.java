package ru.matmex.subscription.services.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.matmex.subscription.services.notifications.email.UserEmailNotificationSender;

import java.util.List;

/**
 * Класс для управления рыссыльщиками уведомлений
 */
@Configuration
public class NotificationSenderManager {
    public List<NotificationSender> getNotificationSenderList() {
        return notificationSenderList;
    }

    private final List<NotificationSender> notificationSenderList;

    @Autowired
    public NotificationSenderManager(List<NotificationSender> notificationSenderList, NotificationSender emailNotificationSender) {
        this.notificationSenderList = notificationSenderList;

        registerNotificationSender(emailNotificationSender);
    }

    /**
     * Зарегистрировать рассыльщика уведомлений
     *
     * @param sender - рассыльщик уведомлений
     */
    public void registerNotificationSender(NotificationSender sender) {
        notificationSenderList.add(sender);
    }
}
