package ru.practicum.main.controller.privateController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.dto.request.UpdateEventUserRequest;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.service.EventService;
import ru.practicum.main.util.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.main.util.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.main.util.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Valid
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventShortDto> getEventsAddedByCurrentUser(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                                 @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return eventService.getEventsAddedByCurrentUser(userId, page);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("fsdf");
        }
        return eventService.saveEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventAddedCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventAddedCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto changeEventAddedCurrentUser(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody UpdateEventUserRequest dto) {
        return eventService.changeEventAddedCurrentUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestsByCurrentUser(@PathVariable Long userId,
                                                                        @PathVariable Long eventId) {
        return eventService.getRequestsByCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(@PathVariable Long userId,
                                                                                 @PathVariable Long eventId,
                                                                                 @Valid @RequestBody EventRequestStatusUpdateRequestDto dto) {
        return eventService.changeStatusOfRequestsByCurrentUser(userId, eventId, dto); // доделать
    }
}
