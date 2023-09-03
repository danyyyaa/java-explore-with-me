package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.entity.Category;
import ru.practicum.main.exception.NotAvailableException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CategoryService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category %s not found", catId)));

        boolean isExist = eventRepository.existsByCategoryId(catId);

        if (isExist) {
            throw new NotAvailableException(String.format("Category %s isn't empty", catId));
        } else {
            categoryRepository.delete(category);
        }
    }

    @Override
    public Category changeCategory(Long catId, Category category) {
        Category updated = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category %s not found", catId)));

        if (category.getName() != null && !category.getName().isBlank()) {
            updated.setName(category.getName());
        }

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Category> getAllCategories(Pageable page) {
        return categoryRepository.findAll(page).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category %s not found", catId)));
    }
}