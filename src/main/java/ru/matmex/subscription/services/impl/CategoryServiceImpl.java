package ru.matmex.subscription.services.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реализация сервиса для операций с категориями
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryModelMapper categoryModelMapper;
    private final SubscriptionService subscriptionService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               UserService userService,
                               @Lazy SubscriptionService subscriptionService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.categoryModelMapper = new CategoryModelMapper();
        this.subscriptionService = subscriptionService;
    }

    @Override
    public List<CategoryModel> getCategoriesByCurrentUsername() {
        List<Category> categories = categoryRepository.findCategoriesByUser(userService.getCurrentUser());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Список категорий пуст!");
        }
        return categories
                .stream().map(categoryModelMapper::map)
                .toList();
    }

    @Override
    public Category getCategory(String name) {
        return categoryRepository
                .findCategoryByNameAndUser(name, userService.getCurrentUser())
                .orElseThrow(() -> new EntityNotFoundException("Подписка " + name + "не найдена!"));
    }

    @Override
    public CategoryModel getCategoryToClient(String name) {
        return categoryModelMapper.map(getCategory(name));
    }

    @Override
    public CategoryModel create(CreateCategoryModel createCategoryModel) {
        if (categoryRepository.existsByNameAndUser(createCategoryModel.name(), userService.getCurrentUser())) {
            throw new EntityExistsException("Сущность с именем " + createCategoryModel.name() + " уже существует");
        }
        Category category = new Category(createCategoryModel.name(), new ArrayList<>(), userService.getCurrentUser());
        categoryRepository.save(category);
        return categoryModelMapper.map(category);
    }

    @Override
    public void createDefaultSubscription(User user) {
        Category category = new Category("default", new ArrayList<>(), user);
        categoryRepository.save(category);
    }

    @Override
    public CategoryModel update(UpdateCategoryModel updateCategoryModel) {
        Category category = categoryRepository
                .findCategoryById(updateCategoryModel.id())
                .orElseThrow(EntityNotFoundException::new);
        category.setName(updateCategoryModel.name());
        categoryRepository.save(category);
        return categoryModelMapper.map(category);
    }

    @Override
    public String delete(Long id) throws EntityNotFoundException {
        if (categoryRepository.existsById(id)) {
            categoryRepository
                    .findCategoryById(id).orElseThrow().getSubscriptions()
                    .forEach(subscription -> subscriptionService.deleteSubscription(subscription.getId())
                    );
            categoryRepository.deleteAllById(Collections.singleton(id));
            return "Категория успешна удалена!";
        } else throw new EntityNotFoundException();
    }
}
