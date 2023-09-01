package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.*;
import ru.practicum.main.entity.*;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.entity.enums.EventRequestStatus;
import ru.practicum.main.entity.enums.EventSort;
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

    @Value("${app.name}")
    private String app;

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
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page);

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventAddedCurrentUser(Long userId, Long eventId) {
        Event event = eventRepository.findEventByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Event with user id %s and eventId %s not found", userId, eventId)));

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

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(UpdateEventUserRequestDto.StateAction.SEND_TO_REVIEW)) {
                event.setState(EventPublishedStatus.PENDING);
            } else {
                event.setState(EventPublishedStatus.CANCELED);
            }
        }

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId)));

        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(Long userId, Long eventId,
                                                                                 EventRequestStatusUpdateRequestDto dto) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId)));

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                EventRequestStatus.CONFIRMED);

        long freePlaces = event.getParticipantLimit() - confirmedRequests;

        if (dto.getStatus().equals(EventRequestStatus.CONFIRMED) && freePlaces <= 0) {
            throw new NotAvailableException("The limit of requests to participate in the event has been reached");
        }

        List<Request> requests = requestRepository.findAllByEventIdAndEventInitiatorIdAndIdIn(eventId,
                userId, dto.getRequestIds());

        setStatus(requests, dto.getStatus(), freePlaces);

        List<ParticipationRequestDto> requestsDto = requests
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> confirmedRequestsDto = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestsDto = new ArrayList<>();

        requestsDto.forEach(el -> {
            if (dto.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                confirmedRequestsDto.add(el);
            } else {
                rejectedRequestsDto.add(el);
            }
        });

        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequestsDto)
                .rejectedRequests(rejectedRequestsDto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventFullDto> getEventsByAdmin(Set<Long> userIds, Set<Long> categoryIds,
                                                     Collection<EventPublishedStatus> states,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Pageable pageable) {
        List<Event> events = eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);

        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(UpdateEventAdminRequestDto.StateAction.PUBLISH_EVENT)) {
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
                .app(app)
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

        Optional<LocalDateTime> start = eventRepository.getStart(eventsId);

        Map<Long, Long> views = new HashMap<>();

        if (start.isPresent()) {
            List<ViewStatsResponseDto> response = statsClient
                    .getStats(start.get(), LocalDateTime.now(), uris, true);

            response.forEach(dto -> {
                String uri = dto.getUri();
                String[] split = uri.split("/");
                String id = split[2];
                Long eventId = Long.parseLong(id);
                views.put(eventId, dto.getHits());
            });
        } else {
            eventsId.forEach(el -> views.put(el, 0L));
        }

        return views;
    }


    private void patchUpdateEvent(UpdateEventRequest dto, Event event) {
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

    private void setStatus(Collection<Request> requests, EventRequestStatus status, long freePlaces) {
        if (status.equals(EventRequestStatus.CONFIRMED)) {
            for (Request request : requests) {
                if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
                    throw new NotAvailableException("Request's status has to be PENDING");
                }
                if (freePlaces > 0) {
                    request.setStatus(EventRequestStatus.CONFIRMED);
                    freePlaces--;
                } else {
                    request.setStatus(EventRequestStatus.REJECTED);
                }
            }
        } else if (status.equals(EventRequestStatus.REJECTED)) {
            requests.forEach(request -> {
                if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
                    throw new NotAvailableException("Request's status has to be PENDING");
                }
                request.setStatus(EventRequestStatus.REJECTED);
            });
        } else {
            throw new NotAvailableException("You must either approve - CONFIRMED" +
                    " or reject - REJECTED the application");
        }
    }
}
