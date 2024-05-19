package ru.matmex.subscription.services.notifications.telegram;

/**
 * Интерфейс Bot определяет операции, необходимые для мессенджер-бота.
 */
public interface Bot {
    /**
     * Отправить сообщение в чат.
     * @param chatId - уникальный идентификатор чата
     * @param textToSend - текст сообщения, который нужно отправить.
     */
    void sendMessage(Long chatId, String textToSend);
}
