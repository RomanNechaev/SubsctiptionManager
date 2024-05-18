package ru.matmex.subscription.services;

import com.google.api.services.calendar.Calendar;
import ru.matmex.subscription.models.subscription.SubscriptionModel;

import java.util.List;

/**
 * Сервис для работы с гугл календарем пользователя
 */
public interface GoogleCalendarService {
    /**
     * Копирует все подписки пользователя в гугл календарь сервиса.
     * Чтобы у пользователя в календаре отображалась сама подписка и ее срок действия, как событие.
     * @param calendar экземпляр гугл календаря
     * @param subscriptions список подписок пользователя
     */
    void copySubscriptionsToCalendar(Calendar calendar, List<SubscriptionModel> subscriptions);
}
