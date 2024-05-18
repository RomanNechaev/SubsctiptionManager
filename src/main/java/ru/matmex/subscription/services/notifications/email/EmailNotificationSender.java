package ru.matmex.subscription.services.notifications.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationSender;

@Service
public class EmailNotificationSender implements NotificationSender {
    private final JavaMailSender notificationMailSender;
    private final SimpleMailMessage templateMessage;
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);
    private final UserService userService;

    @Autowired
    public EmailNotificationSender(JavaMailSender notificationMailSender, UserService userService) {
        this.notificationMailSender = notificationMailSender;
        this.userService = userService;
        this.templateMessage = new SimpleMailMessage();
    }


    @Override
    public void sendNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        String userEmail = userService.getUserModel(notification.getUsername()).email();
        String emailFrom = "petrlovygin@yandex.ru";
        message.setSubject("Уведомление");
        message.setFrom(emailFrom);
        message.setTo(userEmail);
        message.setText(notification.getMessage() + "\nДата отправки события: " + notification.getCurrentDate());
        message.setSentDate(notification.getCurrentDate());
        try {
            notificationMailSender.send(message);
        } catch (MailException exception) {
            logger.error(String.format("Не удалось отправить сообщение на email:%s", userEmail));
            logger.error(exception.getMessage());
        }
    }

}
