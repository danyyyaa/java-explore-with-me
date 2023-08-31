package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.enums.EventRequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Long countAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    List<Request> findAllByEventIdAndEventInitiatorId(Long eventId, Long userId);

    List<Request> findAllByStatusAndEventIdIn(EventRequestStatus status, Collection<Long> eventIds);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(Long eventId, Long userId,
                                                             Collection<Long> requestsId);
}