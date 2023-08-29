package ru.practicum.main.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.request.UpdateEventUserRequestDto;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.service.EventService;
import ru.practicum.main.util.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static ru.practicum.Constant.*;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Valid
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventFullDto> getEventsByAdmin(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime rangeEnd,
                                                     @RequestParam(required = false) Set<EventPublishedStatus> states,
                                                     @RequestParam(required = false) Set<Long> userIds,
                                                     @RequestParam(required = false) Set<Long> categoriesIds) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return eventService.getEventsByAdmin(userIds, categoriesIds, states, rangeStart, rangeEnd, page);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@Positive @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequestDto dto) {
        return eventService.updateEventByAdmin(eventId, dto);
    }
}
