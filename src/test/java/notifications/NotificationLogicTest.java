package notifications;

import org.junit.jupiter.api.Test;
import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationSender;
import ru.matmex.subscription.services.notifications.NotificationSenderManager;
import ru.matmex.subscription.services.notifications.NotificationService;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
class NotificationLogicTest {

    private final NotificationService notificationService;
    private final NotificationSenderManager notificationSenderManager;

    public NotificationLogicTest() {
        NotificationSender fakeSender = notification -> {};
        this.notificationSenderManager = new NotificationSenderManager(
                new ArrayList<>(),
                fakeSender
        );
        this.notificationService = new NotificationService(notificationSenderManager);
    }

    /**
     * Тестирование уведомления рассыльщиков
     */
    @Test
    void testNotifyAllSubscribers() {
        FakeSender sender = new FakeSender();
        Notification testNotification = new Notification("test", 1L);
        Notification testNotification2 = new Notification("test2", 2L);
        notificationService.addNotification(testNotification);
        notificationService.addNotification(testNotification2);
        notificationSenderManager.registerNotificationSender(sender);

        notificationService.notifyAllSubscriber();

        assertThat(sender.getMessagesToSend().size()).isEqualTo(1);

        assertThat(sender.getMessagesToSend().get(0)).isEqualTo(testNotification);
    }

    /**
     * Тестирование уведомления рыссылищиков, если список рыссыльщиков пуст
     */
    @Test
    void testCanNotifyAllSubscribersIfSenderListIsEmpty() {
        FakeSender sender = new FakeSender();

        notificationService.notifyAllSubscriber();

        assertThat(sender.getMessagesToSend()).isEmpty();
    }
}
