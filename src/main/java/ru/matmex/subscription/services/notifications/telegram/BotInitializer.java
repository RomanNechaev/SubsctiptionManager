package ru.matmex.subscription.services.notifications.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.matmex.subscription.models.security.Crypto;
import ru.matmex.subscription.services.UserService;

/**
 * Инициализация телеграмм бота
 */
@Component
public class BotInitializer {
    private final BotConfig botConfig;
    private final UserService userService;
    private final Crypto crypto;

    @Autowired
    public BotInitializer(BotConfig botConfig, UserService userService, Crypto crypto) {
        this.botConfig = botConfig;
        this.userService = userService;
        this.crypto = crypto;
    }

    /**
     * Инициализирует бота при старте или обновлении приложения
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBot telegramBot = new TelegramBot(botConfig, userService, crypto);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
