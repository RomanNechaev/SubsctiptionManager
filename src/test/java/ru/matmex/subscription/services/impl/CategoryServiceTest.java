package ru.matmex.subscription.services.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.matmex.subscription.entities.Category;
import ru.matmex.subscription.entities.User;
import ru.matmex.subscription.models.category.CategoryModel;
import ru.matmex.subscription.models.category.CreateCategoryModel;
import ru.matmex.subscription.models.category.UpdateCategoryModel;
import ru.matmex.subscription.repositories.CategoryRepository;
import ru.matmex.subscription.services.CategoryService;
import ru.matmex.subscription.services.SubscriptionService;
import ru.matmex.subscription.services.UserService;
import ru.matmex.subscription.services.utils.mapping.CategoryModelMapper;
import ru.matmex.subscription.utils.CategoryBuilder;
import ru.matmex.subscription.utils.UserBuilder;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = {CategoryServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final SubscriptionService subscriptionService = Mockito.mock(SubscriptionService.class);

    private final CategoryService categoryService = new CategoryServiceImpl(
            categoryRepository,
            userService,
            subscriptionService);
    ;

    /**
     * Тестирование получения категорий текущего пользователя пользователя
     */
    @Test
    void testCanGetCategoriesByCurrentUsername() {
        User testUser = UserBuilder.anUser().defaultUser();
        List<Category> categories = List.of(
                CategoryBuilder.anCategory().defaultCategory(),
                CategoryBuilder.anCategory()
                        .withName("test2")
                        .withSubscriptions(new ArrayList<>())
                        .withUser(UserBuilder.anUser().defaultUser())
                        .build()
        );

        when(userService.getCurrentUser()).thenReturn(testUser);

        when(categoryRepository.findCategoriesByUser(testUser)).thenReturn(categories);

        List<CategoryModel> categoryModels = categoryService.getCategoriesByCurrentUsername();
        assertThat(categoryModels).isNotNull();
        assertThat(categories.get(0).getName()).isEqualTo(categoryModels.get(0).name());
        assertThat(categories.get(1).getName()).isEqualTo(categoryModels.get(1).name());

    }

    /**
     * Тестирование получения категорий пользователя
     */
    @Test
    void testCanGetCategoryByName() {
        String categoryName = "test";
        Category testCategory = CategoryBuilder.anCategory().defaultCategory();
        User testUser = UserBuilder.anUser().defaultUser();
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findCategoryByNameAndUser(categoryName, testUser)).thenReturn(Optional.of(testCategory));

        Category actualCategory = categoryService.getCategory(categoryName);

        assertThat(actualCategory).isEqualTo(testCategory);

    }

    /**
     * Тестирование создания категории
     */
    @Test
    void testCanCreateCategory() {
        User testUser = UserBuilder.anUser().defaultUser();
        CreateCategoryModel createCategoryModel = new CreateCategoryModel("test");
        given(categoryRepository.existsByNameAndUser("test", testUser)).willReturn(false);
        when(userService.getCurrentUser()).thenReturn(testUser);
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        categoryService.create(createCategoryModel);

        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());

        Category category = categoryArgumentCaptor.getValue();

        assertThat(category.getName()).isSameAs(createCategoryModel.name());

    }

    /**
     * Тестирвоание создание категории по умолчанию
     */
    @Test
    void testCanCreateDefaultCategory() {
        User testUser = UserBuilder.anUser().defaultUser();

        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        categoryService.createDefaultSubscription(testUser);

        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());

        Category category = categoryArgumentCaptor.getValue();

        assertThat(category.getName()).isSameAs("default");

    }

    /**
     * Тестирование создания категории, если она уже существует
     */
    @Test
    void testWillThrowWhenCreateCategoryExists() {
        CreateCategoryModel createCategoryModel = new CreateCategoryModel("test");
        User testUser = UserBuilder.anUser().defaultUser();
        when(userService.getCurrentUser()).thenReturn(testUser);
        given(categoryRepository.existsByNameAndUser("test", testUser)).willReturn(true);

        assertThatThrownBy(() -> categoryService.create(createCategoryModel))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("Сущность с именем " + createCategoryModel.name() + " уже существует");

        verify(categoryRepository, never()).save(any());
    }

    /**
     * Тестирование обновления категории
     */
    @Test
    void testCanUpdateCategory() {
        String newName = "sport";
        String oldName = "life";
        UpdateCategoryModel updateCategoryModel = new UpdateCategoryModel(
                12L,
                newName
        );
        Category category = CategoryBuilder.anCategory()
                .withName(oldName)
                .withSubscriptions(new ArrayList<>())
                .withUser(UserBuilder.anUser().defaultUser())
                .build();

        when(categoryRepository.findCategoryById(12L)).thenReturn(Optional.of(category));
        categoryService.update(updateCategoryModel);

        String updatedName = category.getName();

        assertThat(updatedName).isSameAs(newName);
    }

    /**
     * Тестирование удаления категории
     */
    @Test
    void testCanDeleteCategory() {
        Long categoryId = 12L;
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        when(categoryRepository.
                findCategoryById(categoryId))
                .thenReturn(Optional.of(CategoryBuilder.anCategory().defaultCategory()));
        categoryService.delete(categoryId);

        verify(categoryRepository).deleteAllById(Collections.singleton(categoryId));

    }

    /**
     * Тестирование удаления несуществующей категории
     */
    @Test
    void testWillThrownWhenDeleteCategoryNotFound() {
        Long categoryId = 12L;
        given(categoryRepository.existsById(categoryId)).willReturn(false);

        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    /**
     * Тестирование преобразования сущности категории в DTO
     */
    @Test
    void testCanMapToCategoryModel() {
        Category category = CategoryBuilder.anCategory().defaultCategory();
        User testUser = UserBuilder.anUser().defaultUser();
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findCategoryByNameAndUser(category.getName(), testUser)).thenReturn(Optional.of(category));
        CategoryModel categoryModel = categoryService.getCategoryToClient(category.getName());

        assertThat(categoryModel).
                hasFieldOrPropertyWithValue("name", "test");
    }


}