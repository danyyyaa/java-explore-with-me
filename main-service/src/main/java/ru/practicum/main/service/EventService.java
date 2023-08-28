package ru.practicum.main.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.dto.request.UpdateEventUserRequest;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);
    List<EventShortDto> getEventsAddedByCurrentUser(Long userId, Pageable page);
    EventFullDto getEventAddedCurrentUser(@PathVariable Long userId, @PathVariable Long eventId);
    EventFullDto changeEventAddedCurrentUser(Long userId, Long eventId, UpdateEventUserRequest dto);
    Collection<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId);
    EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequestDto dto);
}
