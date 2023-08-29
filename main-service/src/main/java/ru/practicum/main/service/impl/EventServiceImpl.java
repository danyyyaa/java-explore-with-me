package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.dto.request.UpdateEventUserRequestDto;
import ru.practicum.main.entity.Category;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.Location;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.entity.enums.EventSort;
import ru.practicum.main.entity.enums.StateAction;
import ru.practicum.main.exception.NotAvailableException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.LocationMapper;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.repository.*;
import ru.practicum.main.service.EventService;
import ru.practicum.stats.entity.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventMapper eventMapper;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    //private final StatsClient statsClient;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto dto) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format("Category %s not found", dto.getCategory())));

        Location savedLocation = locationRepository
                .save(locationMapper.toLocation(dto.getLocation()));

        Event event = eventMapper.toEvent(dto, savedLocation, category, EventPublishedStatus.PENDING, initiator);
        event.setCreatedOn(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);


        return eventMapper.toEventFullDto(savedEvent, category, initiator);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsAddedByCurrentUser(Long userId, Pageable page) {
        return eventRepository.findAllByInitiator_Id(userId, page)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventAddedCurrentUser(Long userId, Long eventId) {
        Event event = eventRepository.findEventByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Event with user id %s and eventId %s not found", userId, eventId)));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto changeEventAddedCurrentUser(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId)));

        if (event.getState().equals(EventPublishedStatus.PUBLISHED)) {
            throw new NotAvailableException("Only canceled events or events pending moderation can be changed");
        }

        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new NotAvailableException("The date and time on which the event is scheduled cannot be earlier" +
                    " than two hours from the current moment.");
        }

        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        patchUpdateEvent(dto, event);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId) {
        return requestRepository.findRequestsByUserIdAndEventId(userId, eventId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(Long userId, Long eventId,
                                                                                 EventRequestStatusUpdateRequestDto dto) {
        /*userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Event updated = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId)));

        List<Request> toUpdateRequests = requestRepository.findRequestsByUserIdAndEventId(userId, eventId);


        if (dto.getRequestIds() != null && !dto.getRequestIds().isEmpty()) {
            toUpdateRequests.forEach(request -> request.setStatus(dto.getStatus()));
        }*/


        return null;
    }

    @Override
    public Collection<EventFullDto> getEventsByAdmin(Set<Long> userIds, Set<Long> categoryIds,
                                                     Collection<EventPublishedStatus> states,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Pageable pageable) {
        List<Event> events = eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);
        return events
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequestDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventPublishedStatus.PENDING)) {
                    throw new NotAvailableException(String.format("Event %s has already been published", eventId));
                }
                event.setState(EventPublishedStatus.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (!event.getState().equals(EventPublishedStatus.PENDING)) {
                    throw new NotAvailableException("Event must be in PENDING status");
                }
                event.setState(EventPublishedStatus.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new NotAvailableException("The start date of the modified event must be" +
                    " no earlier than one hour from the publication date");
        }
        patchUpdateEvent(dto, event);
        locationRepository.save(event.getLocation());

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public Collection<EventShortDto> getEventsPublic(String text, Set<Long> categoriesIds, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     boolean onlyAvailable, EventSort sort, Pageable pageable,
                                                     HttpServletRequest httpServletRequest) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeStart cannot be later than rangeEnd");
        }

        List<Event> events = eventRepository.findAllPublic(text, categoriesIds, paid,
                rangeStart, rangeEnd, onlyAvailable, pageable);

        return events
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, String uri, String ip) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (!eventId.equals(event.getInitiator().getId())) {
            throw new ValidationException(String.format(
                    "User %s isn't the initiator the event", event.getInitiator().getId()));
        }

        return eventMapper.toEventFullDto(event);
    }

    private void sendStats(HttpServletRequest httpServletRequest) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("main-service")
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void patchUpdateEvent(UpdateEventUserRequestDto dto, Event event) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Category %s not found", dto.getCategory())));
            event.setCategory(category);
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }
}
