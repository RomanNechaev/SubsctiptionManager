package ru.matmex.subscription.services.impl;

import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.Test;
import ru.matmex.subscription.models.subscription.SubscriptionModel;
import ru.matmex.subscription.services.utils.GoogleEventShaper;
import ru.matmex.subscription.services.utils.mapping.SubscriptionModelMapper;
import ru.matmex.subscription.utils.SubscriptionBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GoogleServiceTest {
    private static final GoogleEventShaper GOOGLE_EVENT_SHAPER = new GoogleEventShaper();
    private static final SubscriptionModelMapper subscriptionModelMapper = new SubscriptionModelMapper();

    /**
     * Тестирование правильного заполнения события на основе подписки
     */
    @Test
    void testDefaultSubscription() {
        SubscriptionModel subscription = subscriptionModelMapper.map(
                SubscriptionBuilder.anSubscription().defaultSubscription());
        Event event = GOOGLE_EVENT_SHAPER.formationEvents(subscription);
        assertThat(event.getSummary()).isEqualTo("test");
        assertThat(event.getDescription()).isEqualTo("Стоимость: 12.0");
        assertFalse(event.isEndTimeUnspecified());
    }

    /**
     * Тестирование корректно заданного временного интервала длительности события
     */
    @Test
    void testTimeInterval() {

        SubscriptionModel subscription = subscriptionModelMapper.map(
                SubscriptionBuilder.anSubscription().defaultSubscription());
        Event event = GOOGLE_EVENT_SHAPER.formationEvents(subscription);

        long expectedStartDate = subscription.paymentDate().getTime() + 123;
        long expectedEndDate = expectedStartDate + 86400000;

        assertThat(event.getStart().getDateTime().getValue()).isEqualTo(expectedStartDate);
        assertThat(event.getEnd().getDateTime().getValue()).isEqualTo(expectedEndDate);
    }

    /**
     * Тестирование корректно заданного временного интервала длительности события
     */
    @Test
    void testIncorrectTimeInterval() {

        SubscriptionModel subscription = subscriptionModelMapper.map(
                SubscriptionBuilder.anSubscription().defaultSubscription());
        Event event = GOOGLE_EVENT_SHAPER.formationEvents(subscription);

        long wrongStartDate = subscription.paymentDate().getTime();
        long wrongEndDate = wrongStartDate + 172800; // 2 дня
        assertThat(event.getStart().getDateTime().getValue()).isNotEqualTo(wrongStartDate);
        assertThat(event.getEnd().getDateTime().getValue()).isNotEqualTo(wrongEndDate);
    }
}
