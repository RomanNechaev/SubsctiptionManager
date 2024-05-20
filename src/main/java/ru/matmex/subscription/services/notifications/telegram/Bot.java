package ru.matmex.subscription.services.notifications.telegram;

public interface Bot {
    void sendMessage(Long chatId, String textToSend);
}
