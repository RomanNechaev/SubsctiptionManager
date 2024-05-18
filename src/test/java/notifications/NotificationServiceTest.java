package notifications;

import org.junit.jupiter.api.Test;
import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationService;

import static org.assertj.core.api.Assertions.*;

class NotificationServiceTest {

    private final NotificationService notificationService;

    public NotificationServiceTest() {
        this.notificationService = new NotificationService();
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
        notificationService.addNotificationSender(sender);

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
