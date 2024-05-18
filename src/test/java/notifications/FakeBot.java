package notifications;

import ru.matmex.subscription.services.notifications.telegram.Bot;

import java.util.ArrayList;
import java.util.List;

public class FakeBot implements Bot {

    private final List<String> texts = new ArrayList<>();

    @Override
    public void sendMessage(Long chatId, String textToSend) {
        texts.add(textToSend);
    }

    public List<String> getTexts() {
        return texts;
    }

    public String getLastMessage() {
        return texts.get(texts.size() - 1);
    }
}
