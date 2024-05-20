package ru.matmex.subscription.services.utils.mapping;

import ru.matmex.subscription.entities.Subscription;
import ru.matmex.subscription.models.subscription.SubscriptionModel;

/**
 * Преобразование сущности подписки в модель подписки
 */
public class SubscriptionModelMapper {
    public SubscriptionModel map(Subscription subscription) {
        return new SubscriptionModel(
                subscription.getId(),
                subscription.getName(),
                subscription.getPaymentDate(),
                subscription.getPrice(),
                subscription.getCategory().getName());
    }
}
