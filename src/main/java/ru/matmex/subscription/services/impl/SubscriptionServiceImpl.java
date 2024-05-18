package ru.matmex.subscription.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.entities.Subscription;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.subscription.CreateSubscriptionModel;
import ru.matmex.subscription.models.subscription.SubscriptionModel;
import ru.matmex.subscription.models.subscription.UpdateSubscriptionModel;
import ru.matmex.subscription.repositories.CategoryRepository;
import ru.matmex.subscription.repositories.SubscriptionRepository;
import ru.matmex.subscription.services.CategoryService;
import ru.matmex.subscription.services.SubscriptionService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.utils.mapping.SubscriptionModelMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Реализация сервиса для операций с подписками
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final SubscriptionModelMapper subscriptionModelMapper;
    private final CategoryRepository categoryRepository;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   CategoryService categoryService, UserService userService,
                                   CategoryRepository categoryRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionModelMapper = new SubscriptionModelMapper();
        this.categoryService = categoryService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<SubscriptionModel> getSubscriptions() {
        return getSubscriptionsByUser(userService.getCurrentUser())
                .stream()
                .map(subscriptionModelMapper::map)
                .toList();
    }

    public List<Subscription> getSubscriptionsByUser(User user) {
        List<Category> categories = categoryRepository.findCategoriesByUser(user);
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Список категорий пуст!");
        }
        return categories
                .stream().map(Category::getSubscriptions)
                .flatMap(Collection::stream)
                .toList();

    }

    public SubscriptionModel getSubscription(String name) {
        return getSubscriptions()
                .stream()
                .filter(sub -> Objects.equals(sub.name(), name))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("нет подписки с таким названием!"));
    }

    @Override
    public SubscriptionModel createSubscription(CreateSubscriptionModel createSubscriptionModel) {
        Category category = categoryService.getCategory(createSubscriptionModel.category());
        Subscription subscription;
        try {
            subscription = new Subscription(
                    createSubscriptionModel.name(),
                    createSubscriptionModel.price(),
                    formatter.parse(createSubscriptionModel.paymentDate()),
                    category,
                    userService.getCurrentUser()
            );
        } catch (ParseException e) {
            logger.error("Ошибка преобразования даты!");
            throw new RuntimeException(e);
        }
        subscriptionRepository.save(subscription);
        return subscriptionModelMapper.map(subscription);
    }

    @Override
    public String deleteSubscription(Long id) throws EntityNotFoundException {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return "Подписка успешна удалена!";
        } else {
            throw new EntityNotFoundException("Нет такой подписки");
        }
    }

    @Override
    public SubscriptionModel updateSubscription(UpdateSubscriptionModel updateSubscriptionModel) {
        Subscription subscription = subscriptionRepository
                .findById(updateSubscriptionModel.id())
                .orElseThrow(EntityNotFoundException::new);
        subscription.setCategory(categoryService.getCategory(updateSubscriptionModel.category()));
        subscription.setName(updateSubscriptionModel.name());
        subscription.setPrice(updateSubscriptionModel.price());
        try {
            subscription.setPaymentDate(formatter.parse(updateSubscriptionModel.paymentDate()));
        } catch (ParseException e) {
            logger.error("Ошибка преобразования даты!");
            throw new RuntimeException(e);
        }
        subscriptionRepository.save(subscription);
        return subscriptionModelMapper.map(subscription);
    }
}
