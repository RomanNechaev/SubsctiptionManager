package ru.matmex.subscription.services.notifications.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Конфигурация отправителя сообщений
 */
@Configuration
public class MailConfiguration {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.protocol}")
    private String protocol;


    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setPort(port);

        Properties properties = mailSender.getJavaMailProperties();

        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        return mailSender;
    }
}
