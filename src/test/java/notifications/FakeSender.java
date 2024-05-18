package notifications;

import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationSender;

import java.util.ArrayList;
import java.util.List;

public class FakeSender implements NotificationSender {
    private final List<Notification> messagesToSend = new ArrayList<>();

    @Override
    public void sendNotification(Notification notification) {
        messagesToSend.add(notification);
    }

    public List<Notification> getMessagesToSend() {
        return messagesToSend;
    }
}
