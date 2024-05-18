package ru.matmex.subscription.services.utils;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Component;
import ru.matmex.subscription.models.subscription.SubscriptionModel;

import java.util.Date;
import java.util.TimeZone;

/**
 * Объект формирующий из полученных данных объекты собыйти для гугл сервисов
 */
@Component
public class GoogleEventShaper {
    /**
     * Начало временного промежутка длительности события
     * Для создания события в гугл-календаре нельзя указывать ровное начало дня (00:00:00)
     * Поэтому добавляется незначительное количество времени от начала дня
     */
    private static final long START_EVENT_TIME = 123;
    /**
     * Окончание временного промежутка длительности события.
     * 24 часа
     */
    private static final long END_EVENT_TIME = 86400000;

    /**
     * Формирование события для календаря на основе подписки пользователя
     * В формат события входят:
     * Название подписки
     * Стоимость подписки
     * Временной промежуток даты оплаты
     */
    public Event formationEvents(SubscriptionModel subscription) {
        Event event = new Event();
        event.setSummary(subscription.name());
        event.setDescription("Стоимость: " + subscription.price().toString());

        Date startDate = new Date(subscription.paymentDate().getTime() + START_EVENT_TIME);
        Date endDate = new Date(startDate.getTime() + END_EVENT_TIME);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));
        return event;
    }
}
