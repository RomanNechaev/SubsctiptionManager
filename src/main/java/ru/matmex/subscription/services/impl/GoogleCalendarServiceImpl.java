package ru.matmex.subscription.services.impl;


import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.models.subscription.SubscriptionModel;
import ru.matmex.subscription.services.GoogleCalendarService;
import ru.matmex.subscription.services.impl.exception.CalendarException;
import ru.matmex.subscription.services.utils.GoogleUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    /**
     * Идентификатор календаря.
     * primary - ключевое слово,
     * позволяет получить доступ к основному календарю текущего пользователя, вошедшего в систему
     */
    private static final String CALENDAR_ID = "primary";
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);
    private final GoogleUtils googleUtils = new GoogleUtils();

    /**
     * Если какая-то подписка уже имеется в календаря, то она не вставляется.
     */
    @Override
    public void copySubscriptionsToCalendar(Calendar calendar, List<SubscriptionModel> subscriptions) {
        subscriptions.removeIf(subscription -> getEventsFromCalendar(calendar).stream().
                anyMatch(event -> Objects.equals(event.getSummary(), subscription.name())));
        for (SubscriptionModel subscription : subscriptions) {
            Event newEvent = googleUtils.subscriptionFormationAsEvents(subscription);
            insertSubscriptionsInCalendar(calendar, newEvent);
        }
    }

    /**
     * Вставка в гугл-календарь подписки пользователя, как событие,
     */
    private void insertSubscriptionsInCalendar(Calendar calendar, Event newEvent) throws CalendarException {
        try {
            calendar.events().insert(CALENDAR_ID, newEvent).execute();
        } catch (IOException e) {
            logger.error("Не удалось вставить подписку подписку в календарь пользователя");
            throw new CalendarException("Не удалось вставить подписку в календарь" + e.getMessage());
        }
    }

    /**
     * @param calendar календарь
     * @return все события в гугл календаре
     */
    private List<Event> getEventsFromCalendar(Calendar calendar) {
        try {
            return calendar.events().list(CALENDAR_ID).execute().getItems();
        } catch (IOException e) {
            logger.error("Не удалось получить события у гугл календаря");
            throw new CalendarException("Не удалось получить события у гугл календаря" + e.getMessage());
        }
    }
}