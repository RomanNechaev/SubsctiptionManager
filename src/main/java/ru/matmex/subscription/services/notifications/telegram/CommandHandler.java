package ru.matmex.subscription.services.notifications.telegram;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.security.Crypto;
import ru.matmex.subscription.services.UserService;


/**
 * Обработка сообщений, поступаемые от пользователя
 */
public class CommandHandler {
    private final Bot bot;

    public CommandHandler(Bot bot) {
        this.bot = bot;
    }

    /**
     * Обработка сообщений от пользователя
     *
     * @param messageText - сообщения от пользователя
     * @param chatId      - id чата в тг
     * @param userService - сервис для операция с пользователями
     * @param crypto      - сервис для шифрования/дешифрования текста
     * @return получилось ли привязать бота
     */
    public boolean processCommand(String messageText, Long chatId, UserService userService, Crypto crypto) {
        if (messageText.equals("/start")) {
            bot.sendMessage(chatId, "Для привязки tg, введите данные в следующем формате:\n Username:secretKey");
            return false;
        } else {
            try {
                String[] splitted = messageText.split(":");
                validateInputMessage(splitted);
                String username = splitted[0];
                String key = splitted[1];
                User currentUser = userService.getUser(username);
                String decodedKey = new String(crypto.decrypt(currentUser.getTelegramSecretKey()));
                boolean match = decodedKey.equals(key);
                if (currentUser.getTelegramChatId() == null && match) {
                    bot.sendMessage(chatId, "Вы успешно привязали бота!");
                    userService.setTelegramChatId(currentUser.getUsername(), chatId);
                    return true;
                } else {
                    if (!match) {
                        bot.sendMessage(chatId, "Неправильный секретный ключ!");
                        return false;
                    }
                    if (currentUser.getTelegramChatId() != null) {
                        bot.sendMessage(chatId, "Вы уже привязали бота!");
                        return false;
                    }
                }
            } catch (IllegalArgumentException e) {
                bot.sendMessage(chatId, "Неправильный формат ввода!!!");
            } catch (UsernameNotFoundException e) {
                bot.sendMessage(chatId, "Пользователь не найден! Попробуй еще раз");
                return false;
            }
        }
        return false;
    }

    private void validateInputMessage(String[] splitted) throws IllegalArgumentException {
        if (splitted.length != 2) {
            throw new IllegalArgumentException();
        }
    }
}
