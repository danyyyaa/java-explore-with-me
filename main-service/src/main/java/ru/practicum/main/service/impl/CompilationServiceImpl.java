package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.entity.Compilation;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.enums.EventRequestStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.service.CompilationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<CompilationDto> getAllCompilations(Pageable pageable, Boolean pinned) {
        List<Compilation> result = pinned != null
                ? compilationRepository.findByPinned(pinned, pageable)
                : compilationRepository.findAll(pageable).getContent();

        return result.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId)));

        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        Set<Long> eventsId = newCompilationDto.getEvents();
        if (eventsId != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventsId));
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return toCompilationDto(savedCompilation);
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

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            toUpdate.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            toUpdate.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Long> eventsId = dto.getEvents();
            Collection<Event> events = eventRepository.findAllByIdIn(eventsId);
            toUpdate.setEvents(new HashSet<>(events));
        }

        return toCompilationDto(toUpdate);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = compilationMapper.toCompilationDto(compilation);
        setConfirmedRequestsToEvent(dto);

        return dto;
    }

    private void setConfirmedRequestsToEvent(CompilationDto dto) {
        Set<EventShortDto> compilationEvents = dto.getEvents();
        if (compilationEvents != null) {
            List<Long> eventIds = new ArrayList<>();

            compilationEvents.forEach(el -> eventIds.add(el.getId()));

            List<Request> confirmedRequests = requestRepository.findAllByStatusAndEventIdIn(
                    EventRequestStatus.CONFIRMED, eventIds);

            Map<Long, Long> requests = confirmedRequests.stream()
                    .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

            compilationEvents.forEach(eventShortDto ->
                    eventShortDto.setConfirmedRequests(requests.getOrDefault(eventShortDto.getId(), 0L)));
        }
    }
}
