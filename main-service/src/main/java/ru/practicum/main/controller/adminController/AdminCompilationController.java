package ru.practicum.main.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Valid
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.saveCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@Positive @PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto changeCompilation(@Positive @PathVariable Long compId,
                                            /*@Valid*/ @RequestBody UpdateCompilationRequest dto) {
        return compilationService.changeCompilation(compId, dto);
    }
}
