package ru.practicum.main.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    Collection<CompilationDto> getAllCompilations(Pageable pageable);

    CompilationDto getCompilationById(Long compId);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto changeCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}