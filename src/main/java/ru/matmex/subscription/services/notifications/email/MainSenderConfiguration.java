package ru.matmex.subscription.services.notifications.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Конфигурация отправителя сообщений
 */

public class MainSenderConfiguration {
    @Bean
    public JavaMailSender getJavaMailSender(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.protocol}") String protocol) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setPort(port);

        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");

        return mailSender;
    }

}
