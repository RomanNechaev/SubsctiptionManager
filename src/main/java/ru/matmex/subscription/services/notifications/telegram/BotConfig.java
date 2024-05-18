package ru.matmex.subscription.services.notifications.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация телеграмм бота
 */
@Configuration
public class BotConfig {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String token;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }
}
