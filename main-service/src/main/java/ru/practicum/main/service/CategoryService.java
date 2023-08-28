package ru.practicum.main.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.entity.Category;

import java.util.Collection;

public interface CategoryService {
    Category saveCategory(Category category);
    void deleteCategoryById(Long catId);
    Category changeCategory(Long catId, Category category);
    Collection<Category> getAllCategories(Pageable page);
    Category getCategoryById(Long catId);
}
