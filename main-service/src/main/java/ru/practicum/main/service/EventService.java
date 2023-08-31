package ru.practicum.main.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.dto.request.UpdateEventUserRequestDto;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.entity.enums.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    Collection<EventShortDto> getEventsAddedByCurrentUser(Long userId, Pageable page);

    EventFullDto getEventAddedCurrentUser(@PathVariable Long userId, @PathVariable Long eventId);

    EventFullDto changeEventAddedCurrentUser(Long userId, Long eventId, UpdateEventUserRequestDto dto);

    Collection<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequestDto dto);

    Collection<EventFullDto> getEventsByAdmin(Set<Long> userIds, Set<Long> categoryIds,
                                              Collection<EventPublishedStatus> states,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Pageable pageable);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequestDto dto);

    Collection<EventShortDto> getEventsPublic(String text, Set<Long> categoriesIds, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              boolean onlyAvailable, EventSort sort, Pageable pageable,
                                              HttpServletRequest httpServletRequest);

    EventFullDto getEventByIdPublic(Long eventId, String uri, String ip);
}