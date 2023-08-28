package ru.practicum.main.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.entity.Category;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Valid
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody NewCategoryDto dto) {
        Category category =  categoryService.saveCategory(categoryMapper.toCategory(dto));
        return categoryMapper.toCategoryResponseDto(category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto changeCategory(@PathVariable Long catId,
                                      @RequestBody NewCategoryDto dto) {
        Category category = categoryService
                .changeCategory(catId, categoryMapper.toCategory(dto));
        return categoryMapper.toCategoryResponseDto(category);
    }
}

