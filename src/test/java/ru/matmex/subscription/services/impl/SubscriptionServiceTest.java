package ru.matmex.subscription.services.impl;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
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
import ru.matmex.subscription.utils.CategoryBuilder;
import ru.matmex.subscription.utils.SubscriptionBuilder;
import ru.matmex.subscription.utils.UserBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {SubscriptionServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    private final SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
    private final CategoryService categoryService = Mockito.mock(CategoryService.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final SubscriptionModelMapper subscriptionModelMapper = new SubscriptionModelMapper();
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

    private final Subscription defaultSubscription = SubscriptionBuilder.anSubscription().defaultSubscription();
    ;
    private final SubscriptionService subscriptionService = new SubscriptionServiceImpl(
            subscriptionRepository,
            categoryService,
            userService,
            categoryRepository);

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    /**
     * Тестирование получения всех подписок подписок определенного пользователя
     */
    @Test
    void testCanGetSubscriptionsByUser() throws ParseException {
        User testUser = UserBuilder.anUser().defaultUser();
        List<Subscription> subscriptionList = List.of(
                defaultSubscription,
                SubscriptionBuilder.anSubscription()
                        .withName("test2")
                        .withCategory(CategoryBuilder.anCategory().defaultCategory())
                        .withPrice(15.0)
                        .withPaymentDate(formatter.parse("12-03-2023"))
                        .withUser(testUser)
                        .build());

        Category category = CategoryBuilder.anCategory()
                .withName("test")
                .withUser(testUser)
                .withSubscriptions(subscriptionList)
                .build();

        when(categoryRepository.findCategoriesByUser(testUser)).thenReturn(List.of(category));

        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUser(testUser);

        assertThat(subscriptionList.size()).isEqualTo(subscriptions.size());

        verify(categoryRepository).findCategoriesByUser(testUser);

    }

    /**
     * Тестирования получения всех подписок текузего пользователя
     */
    @Test
    void testCanGetAllSubscriptions() throws ParseException {
        User testUser = UserBuilder.anUser().defaultUser();
        List<Subscription> subscriptionList = List
                .of(defaultSubscription,
                        SubscriptionBuilder.anSubscription()
                                .withName("test2")
                                .withCategory(CategoryBuilder.anCategory().defaultCategory())
                                .withPrice(15.0)
                                .withPaymentDate(formatter.parse("12-03-2023"))
                                .withUser(testUser)
                                .build());

        Category category = CategoryBuilder.anCategory()
                .withName("test")
                .withUser(testUser)
                .withSubscriptions(subscriptionList)
                .build();

        when(userService.getCurrentUser()).thenReturn(testUser);

        when(categoryRepository.findCategoriesByUser(testUser)).thenReturn(List.of(category));

        List<SubscriptionModel> subscriptionModelList = subscriptionService.getSubscriptions();
        assertThat(subscriptionList.stream()
                .map(subscriptionModelMapper::map)
                .toList())
                .isEqualTo(subscriptionModelList);
    }

    /**
     * Тестирование получения подписки по названию
     */
    @Test
    void testCanGetSubscriptionByName() throws ParseException {
        User testUser = UserBuilder.anUser().defaultUser();

        List<Subscription> subscriptionList = List
                .of(defaultSubscription,
                        SubscriptionBuilder.anSubscription()
                                .withName("test2")
                                .withCategory(CategoryBuilder.anCategory().defaultCategory())
                                .withPrice(15.0)
                                .withPaymentDate(formatter.parse("12-03-2023"))
                                .withUser(testUser)
                                .build());

        Category category = CategoryBuilder.anCategory()
                .withName("test")
                .withUser(testUser)
                .withSubscriptions(subscriptionList)
                .build();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findCategoriesByUser(testUser)).thenReturn(List.of(category));
        SubscriptionModel actualSubscription = subscriptionService.getSubscription(testUser.getUsername());

        assertThat(defaultSubscription.getName()).isEqualTo(actualSubscription.name());
    }

    /**
     * Тестирование создания подписки
     */
    @Test
    void testCanCreateSubscription() {
        CreateSubscriptionModel createSubscriptionModel = new CreateSubscriptionModel(
                "test", "12-03-2013", 123.0, "Test");

        when(categoryService.getCategory(any())).thenReturn(CategoryBuilder.anCategory().defaultCategory());
        subscriptionService.createSubscription(createSubscriptionModel);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);

        verify(subscriptionRepository, times(1)).save(subscriptionArgumentCaptor.capture());

        Subscription subscription = subscriptionArgumentCaptor.getValue();

        assertThat(subscription.getName()).isSameAs(createSubscriptionModel.name());
    }

    /**
     * Тестирование удаления подписки
     */
    @Test
    void testCanDeleteSubscription() {
        Long subscriptionId = 12L;
        given(subscriptionRepository.existsById(subscriptionId)).willReturn(true);

        subscriptionService.deleteSubscription(subscriptionId);

        verify(subscriptionRepository).deleteById(subscriptionId);
    }

    /**
     * Тестирование удаления несуществующей подпсики
     */
    @Test
    void testWillThrowWhenDeleteSubscriptionNotFound() {
        Long subscriptionId = 12L;
        given(subscriptionRepository.existsById(subscriptionId)).willReturn(false);

        assertThatThrownBy(() -> subscriptionService.deleteSubscription(subscriptionId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Нет такой подписки");

        verify(subscriptionRepository, never()).deleteById(any());

    }

    /**
     * Тестирование обновления подписки
     */
    @Test
    void testCanUpdateSubscription() throws ParseException {
        String newName = "spotify";
        Long subscriptionId = 12L;
        UpdateSubscriptionModel updateSubscriptionModel = new UpdateSubscriptionModel(
                subscriptionId,
                newName,
                "12-03-2023",
                123.0,
                "music");
        String oldName = "yandex";
        Subscription subscription = SubscriptionBuilder.anSubscription()
                .withName(oldName)
                .withPrice(123.0)
                .withCategory(CategoryBuilder.anCategory().defaultCategory())
                .withUser(UserBuilder.anUser().defaultUser())
                .withPaymentDate(formatter.parse("12-03-2023"))
                .build();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(categoryService.getCategory(any())).thenReturn(CategoryBuilder.anCategory().defaultCategory());
        subscriptionService.updateSubscription(updateSubscriptionModel);

        String updatedName = subscription.getName();

        assertThat(updatedName).isSameAs(newName);
    }
}