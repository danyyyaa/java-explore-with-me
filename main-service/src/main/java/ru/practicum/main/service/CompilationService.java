package ru.practicum.main.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.entity.Compilation;

import java.util.Collection;

public interface CompilationService {
    Collection<Compilation> getAllCompilations(Pageable pageable);

    Compilation getCompilationById(Long compId);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto changeCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

}
