package notifications;

import org.junit.jupiter.api.Test;
import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationBroker;

import static org.assertj.core.api.Assertions.*;

public class NotificationBrokerTest {
    NotificationBroker notificationBroker = NotificationBroker.getInstance();

    /**
     * Тестирование уведомления рассыльщиков
     */
    @Test
    void testNotifyAllSubscribers() {
        FakeSender sender = new FakeSender();
        Notification testNotification = new Notification("test", "testUser");
        Notification testNotification2 = new Notification("test2", "testUser2");
        notificationBroker.addNotification(testNotification);
        notificationBroker.addNotification(testNotification2);
        notificationBroker.addNotificationSender(sender);

        notificationBroker.notifyAllSubscriber();

        assertThat(sender.getMessagesToSend().size()).isEqualTo(1);

        assertThat(sender.getMessagesToSend().get(0)).isEqualTo(testNotification);
    }

    /**
     * Тестирование уведомления рыссылищиков, если список рыссыльщиков пуст
     */
    @Test
    void testCanNotifyAllSubscribersIfSenderListIsEmpty() {
        FakeSender sender = new FakeSender();

        notificationBroker.notifyAllSubscriber();

        assertThat(sender.getMessagesToSend()).isEmpty();
    }
}
