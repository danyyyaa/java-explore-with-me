package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.entity.Compilation;
import ru.practicum.main.entity.Event;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CompilationService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Compilation> getAllCompilations(Pageable pageable) {
        return compilationRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId)));
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        List<Event> newEvents = eventRepository.findAllById(newCompilationDto.getEvents());

        Compilation savedCompilation = compilationRepository.save(
                compilationMapper.toCompilation(newCompilationDto, newEvents));

        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId)));

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto changeCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation toUpdate = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId)));

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Collection<Event> updatedEvents = eventRepository.findAllById(dto.getEvents());
            toUpdate.setEvents((Set<Event>) updatedEvents);
        }
        if (dto.getPinned() != null) {
            toUpdate.setPinned(dto.getPinned());
        }
        if (!dto.getTitle().isBlank()) {
            toUpdate.setTitle(dto.getTitle());
        }

        return compilationMapper.toCompilationDto(
                compilationRepository.save(toUpdate));
    }
}
