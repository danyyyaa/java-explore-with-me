package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.enums.EventRequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select r" +
            " from Request r" +
            " where r.event.id = :eventId " +
            " and r.requester.id = :userId")
    List<Request> findRequestsByUserIdAndEventId(@Param("userId") Long userId,
                                                 @Param("eventId") Long eventId);

    List<Request> findByRequesterId(Long userId);

    Long countAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    List<Request> findAllByStatusAndEventIdIn(EventRequestStatus status, Collection<Long> eventIds);
}
