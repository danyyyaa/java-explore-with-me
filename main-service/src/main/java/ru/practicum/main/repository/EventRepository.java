package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.enums.EventPublishedStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long id, Pageable pageable);

    @Query("select e" +
            " from Event e" +
            " where e.initiator.id = :initiatorId" +
            " and e.id = :eventId")
    Optional<Event> findEventByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId,
                                                     @Param("eventId") Long eventId);

    @Query("select e from Event e " +
            "where (coalesce(:users, null) is null or e.initiator.id in :userIds) " +
            "and (coalesce(:states, null) is null or e.state in :states) " +
            "and (coalesce(:categories, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) ")
    List<Event> findByAdmin(@Param("userIds") Collection<Long> userIds,
                            @Param("states") Collection<EventPublishedStatus> states,
                            @Param("categoryIds") Collection<Long> categoryIds,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, NULL) IS NULL OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')) OR lower(e.description) LIKE lower(concat('%', :text, '%')))) " +
            "AND (COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:paid, NULL) IS NULL OR e.paid = :paid) " +
            "AND e.eventDate >= :rangeStart " +
            "AND (COALESCE(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.id IN " +
            "(SELECT r.event.id " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id " +
            "HAVING e.participantLimit - count(id) > 0 " +
            "ORDER BY COUNT(r.id))) ")
    List<Event> findAllPublic(@Param("text") String text, @Param("categories") Set<Long> categoriesIds,
                              @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable,
                              Pageable pageable);
}
