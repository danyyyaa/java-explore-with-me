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
import ru.practicum.main.entity.*;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.entity.enums.EventRequestStatus;
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
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.ViewStatsResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
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
    private final StatsClient statsClient;

    public static final LocalDateTime START = LocalDateTime.of(2000, 1, 1, 0, 0);

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

        //return eventMapper.toEventFullDto(savedEvent, category, initiator);

        EventFullDto result = eventMapper.toEventFullDto(savedEvent, category, initiator);
        //fillViews(Collections.singletonList(result));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsAddedByCurrentUser(Long userId, Pageable page) {
        /*List<EventShortDto> result = eventRepository.findAllByInitiator_Id(userId, page)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());*/

        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page);

       /* List<EventShortDto> events = eventRepository.findAllByInitiator_Id(userId, page)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(events
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        events.forEach(el -> el.setViews(eventsViews.getOrDefault(el.getId(), 0L)));

        return events;*/

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventAddedCurrentUser(Long userId, Long eventId) {
        /*Event event = eventRepository.findEventByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Event with user id %s and eventId %s not found", userId, eventId)));
        return eventMapper.toEventFullDto(event);*/

        Event event = eventRepository.findEventByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Event with user id %s and eventId %s not found", userId, eventId)));

        /*Map<Long, Long> eventsViews = getViews(List.of(event.getId()));

        EventFullDto result = eventMapper.toEventFullDto(event);

        result.setViews(eventsViews.getOrDefault(result.getId(), 0L));

        return result;*/

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    public EventFullDto changeEventAddedCurrentUser(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId)));

        if (event.getState().equals(EventPublishedStatus.PUBLISHED)) {
            throw new NotAvailableException("Only canceled events or events pending moderation can be changed");
        }

        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        patchUpdateEvent(dto, event);

        if (dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventPublishedStatus.PENDING);
        } else {
            event.setState(EventPublishedStatus.CANCELED);
        }

        //return eventMapper.toEventFullDto(eventRepository.save(event));

        /*Map<Long, Long> eventsViews = getViews(List.of(event.getId()));

        EventFullDto result = eventMapper.toEventFullDto(event);

        result.setViews(eventsViews.getOrDefault(result.getId(), 0L));

        return result;*/
        return mapToEventFullDto(List.of(event)).get(0);
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
    @Transactional(readOnly = true)
    public Collection<EventFullDto> getEventsByAdmin(Set<Long> userIds, Set<Long> categoryIds,
                                                     Collection<EventPublishedStatus> states,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Pageable pageable) {
        List<Event> events = eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);

        /*return events
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());*/
        return mapToEventFullDto(events);
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

        // return eventMapper.toEventFullDto(event);

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
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

        sendStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        /*return events
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());*/
        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, String uri, String ip) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (!event.getState().equals(EventPublishedStatus.PUBLISHED)) {
            throw new NotFoundException(String.format("Event %s not published", eventId));
        }

        sendStats(uri, ip);

        // return eventMapper.toEventFullDto(event);
        return mapToEventFullDto(List.of(event)).get(0);
    }

    private List<EventFullDto> mapToEventFullDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventFullDto> dtos = events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(el -> {
            el.setViews(eventsViews.getOrDefault(el.getId(), 0L));
            el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L));
        });

        return dtos;

        /*List<EventFullDto> dtos = events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(dtos
                .stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList()));

        dtos.forEach(el -> el.setViews(eventsViews.getOrDefault(el.getId(), 0L)));

        return dtos;*/
    }

    private List<EventShortDto> mapToEventShortDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventShortDto> dtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(el -> {
            el.setViews(eventsViews.getOrDefault(el.getId(), 0L));
            el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L));
        });

        return dtos;



        /*List<EventShortDto> dtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(dtos
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        dtos.forEach(el -> el.setViews(eventsViews.getOrDefault(el.getId(), 0L)));*/

        /*Map<Long, Long> confirmedRequests = getConfirmedRequests(dtos
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        dtos.forEach(el -> el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L)));*/

        //return dtos;
    }

    private Map<Long, Long> getConfirmedRequests(Collection<Long> eventsId) {
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(EventRequestStatus.CONFIRMED, eventsId);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }

    private void sendStats(String uri, String ip) {
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app("main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.createHit(endpointHitRequestDto);
    }

    private Map<Long, Long> getViews(Collection<Long> eventsId) {
        List<String> uris = eventsId
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<ViewStatsResponseDto> response = statsClient.getStats(START, LocalDateTime.now(), uris, true);

        Map<Long, Long> views = new HashMap<>();

        response.forEach(dto -> {
            String uri = dto.getUri();
            String[] split = uri.split("/");
            String id = split[2];
            Long eventId = Long.parseLong(id);
            views.put(eventId, dto.getHits());
        });

        return views;
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
