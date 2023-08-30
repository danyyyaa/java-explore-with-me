package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventPublishedStatus;
import ru.practicum.main.entity.enums.EventRequestStatus;
import ru.practicum.main.exception.NotAvailableException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Request> getRequestsToParticipateInOtherEvents(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        return requestRepository.findByRequesterId(userId);
    }

    @Override
    public Request saveUserRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new NotAvailableException("Event initiator cannot add a request to participate in their event");
        }
        if (!event.getState().equals(EventPublishedStatus.PUBLISHED)) {
            throw new NotAvailableException("Cannot participate in an unpublished event");
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                EventRequestStatus.CONFIRMED);

        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            throw new NotAvailableException(("Limit of requests for participation has been exceeded"));
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(!event.isRequestModeration() || event.getParticipantLimit() == 0
                        ? EventRequestStatus.CONFIRMED
                        : EventRequestStatus.PENDING)
                .build();

        return requestRepository.save(request);
    }

    @Override
    public Request cancelOwnEvent(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request %s not found", requestId)));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException(
                    String.format("User %s didn't apply for participation %s", userId, requestId));
        }
        request.setStatus(EventRequestStatus.CANCELED);

        return requestRepository.save(request);
    }
}
