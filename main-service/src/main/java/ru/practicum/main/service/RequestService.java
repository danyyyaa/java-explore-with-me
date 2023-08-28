package ru.practicum.main.service;

import ru.practicum.main.entity.Request;

import java.util.Collection;

public interface RequestService {
    Collection<Request> getRequestsToParticipateInOtherEvents(Long userId);

    Request saveUsersRequest(Long userId, Long eventId);

    Request cancelOwnEvent(Long userId, Long requestId);
}
