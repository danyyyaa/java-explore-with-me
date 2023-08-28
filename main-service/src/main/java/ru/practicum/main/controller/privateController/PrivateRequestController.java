package ru.practicum.main.controller.privateController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.entity.Request;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.service.RequestService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Valid
public class PrivateRequestController {
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @GetMapping
    public Collection<ParticipationRequestDto> getRequestsToParticipateInOtherEvents(@PathVariable Long userId) {
        return requestService.getRequestsToParticipateInOtherEvents(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveUsersRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        Request request = requestService.saveUsersRequest(userId, eventId);
        return requestMapper.toParticipationRequestDto(request);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelOwnEvent(@PathVariable Long userId, @PathVariable Long requestId) {
        Request request = requestService.cancelOwnEvent(userId, requestId);
        return requestMapper.toParticipationRequestDto(request);
    }
}
