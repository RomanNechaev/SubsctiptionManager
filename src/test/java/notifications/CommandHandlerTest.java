package notifications;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.security.Crypto;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.notifications.telegram.CommandHandler;
import ru.matmex.subscription.utils.UserBuilder;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

public class CommandHandlerTest {
    private final UserService userService = Mockito.mock(UserService.class);
    private final Crypto crypto = new Crypto("supersecretahaha");
    private final FakeBot fakeBot = new FakeBot();
    private final CommandHandler handler = new CommandHandler(fakeBot);

    /**
     * Тестирование на корректное стартовое сообщение
     */
    @Test
    void testStartMessage() {
        handler.processCommand("/start", 1L, userService, crypto);
        assertThat(fakeBot.getLastMessage()).isEqualTo("Для привязки tg, введите данные в следующем формате:\n Username:secretKey");
    }

    /**
     * Тестирование, если пользователь ввел неправильный секретный ключ от телеграмма
     */
    @Test
    void testIfSecretKeyIsNotValid() {
        User testUser = UserBuilder.anUser()
                .withUsername("test")
                .withEmail("test@gmail.com")
                .withPassword("123")
                .withSecretKey(crypto.encrypt("123".getBytes(StandardCharsets.UTF_8)))
                .build();

        Mockito.when(userService.getUser("test")).thenReturn(testUser);

        handler.processCommand("test:1244", 1L, userService, crypto);

        assertThat(fakeBot.getLastMessage()).isEqualTo("Неправильный секретный ключ!");
    }

    /**
     * Тестирование, если пользователь уже привязал бота
     */
    @Test
    void testIfUserIsLinkedToTg() {
        byte[] secretKey = crypto.encrypt("123".getBytes(StandardCharsets.UTF_8));
        User testUser = UserBuilder.anUser()
                .withUsername("test")
                .withEmail("test@gmail.com")
                .withPassword("123")
                .withSecretKey(secretKey)
                .build();
        testUser.setTelegramChatId(123L);

        Mockito.when(userService.getUser("test")).thenReturn(testUser);

        handler.processCommand("test:123",12L,userService,crypto);

        assertThat(fakeBot.getLastMessage()).isEqualTo("Вы уже привязали бота!");
    }

    /**
     * Тестирование, если пользователь ввел данные в неправильном формате
     */
    @Test
    void testIfInputIsNotValid() {
        byte[] secretKey = crypto.encrypt("123".getBytes(StandardCharsets.UTF_8));
        User testUser = UserBuilder.anUser()
                .withUsername("test")
                .withEmail("test@gmail.com")
                .withPassword("123")
                .withSecretKey(secretKey)
                .build();
        testUser.setTelegramChatId(123L);

        Mockito.when(userService.getUser("test")).thenReturn(testUser);

        handler.processCommand("test123",12L,userService,crypto);

        assertThat("Неправильный формат ввода!!!").isEqualTo(fakeBot.getLastMessage());
    }

}
