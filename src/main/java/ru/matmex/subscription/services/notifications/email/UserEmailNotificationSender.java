package ru.matmex.subscription.services.notifications.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.notifications.Notification;
import ru.matmex.subscription.services.notifications.NotificationSender;

@Service
public class UserEmailNotificationSender implements NotificationSender {
    private final JavaMailSender notificationMailSender;
    private final SimpleMailMessage templateMessage;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserEmailNotificationSender.class);

    @Value("${spring.mail.username}")
    private String emailSender;

    @Autowired
    protected UserEmailNotificationSender(JavaMailSender notificationMailSender, UserService userService) {
        this.notificationMailSender = notificationMailSender;
        this.userService = userService;
        this.templateMessage = new SimpleMailMessage();
    }


    @Override
    public void sendNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        String emailTo =  userService.getUser(notification.getUserId()).getEmail();
        message.setSubject("Уведомление");
        message.setFrom(emailSender);
        message.setTo(emailTo);
        message.setText(notification.getMessage() + "\nДата отправки события: " + notification.getDate());
        message.setSentDate(notification.getDate());
        try {
            notificationMailSender.send(message);
        } catch (MailException exception) {
            logger.error(String.format("Не удалось отправить сообщение на email:%s", emailTo), exception);
        }
    }

}
