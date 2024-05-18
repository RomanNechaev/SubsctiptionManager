package ru.matmex.subscription.services.notifications.telegram;

/**
 * Контракт для бота
 */
public interface Bot {
    void sendMessage(Long chatId, String textToSend);
}
