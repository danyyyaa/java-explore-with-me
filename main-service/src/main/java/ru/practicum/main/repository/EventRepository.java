package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long id, Pageable pageable);

    @Query("select e" +
            " from Event e" +
            " where e.initiator.id = :initiatorId" +
            " and e.id = :eventId")
    Optional<Event> findEventByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId,
                                                     @Param("eventId") Long eventId);
}
