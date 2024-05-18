package ru.matmex.subscription.services;

import com.google.api.services.calendar.Calendar;
import ru.matmex.subscription.models.subscription.SubscriptionModel;

import java.util.List;

/**
 * Сервис для работы с гугл календарем пользователя
 */
public interface GoogleCalendarService {
    /**
     * Копировать информацию о подписках пользователя в гугл календарь.
     * @param calendar календарь пользователя
     * @param subscriptions список подписок пользователя
     */
    void copySubscriptionsToCalendar(Calendar calendar, List<SubscriptionModel> subscriptions);
}
