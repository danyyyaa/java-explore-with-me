package ru.practicum.main.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.entity.Compilation;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.service.CompilationService;
import ru.practicum.main.util.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.main.util.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.main.util.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Valid
public class PublicCompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping
    public Collection<CompilationDto> getAllCompilations(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return compilationService.getAllCompilations(page)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        Compilation compilation = compilationService.getCompilationById(compId);
        return compilationMapper.toCompilationDto(compilation);
    }
}
