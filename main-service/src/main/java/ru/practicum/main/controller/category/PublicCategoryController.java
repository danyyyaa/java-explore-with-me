package ru.practicum.main.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aspect.ToLog;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.entity.Category;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.service.CategoryService;
import ru.practicum.main.util.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Valid
@ToLog
public class PublicCategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return categoryService.getAllCategories(page)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@Positive @PathVariable Long catId) {
        Category category = categoryService.getCategoryById(catId);
        return categoryMapper.toCategoryDto(category);
    }
}
