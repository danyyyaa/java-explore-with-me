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
    boolean existsByCategoryId(Long categoryId);

    List<Event> findAllByInitiator_Id(Long id, Pageable pageable);

    List<Event> findAllByIdIn(Collection<Long> eventsId);

    @Query("select e" +
            " from Event e" +
            " where e.initiator.id = :initiatorId" +
            " and e.id = :eventId")
    Optional<Event> findEventByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId,
                                                     @Param("eventId") Long eventId);

    @Query("select e from Event e " +
            "where (coalesce(:userIds, null) is null or e.initiator.id in :userIds) " +
            "and (coalesce(:states, null) is null or e.state in :states) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) ")
    List<Event> findByAdmin(@Param("userIds") Collection<Long> userIds,
                            @Param("states") Collection<EventPublishedStatus> states,
                            @Param("categoryIds") Collection<Long> categoryIds,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            Pageable pageable);

    @Query("select e from Event e " +
            "where e.state = 'PUBLISHED' " +
            "and (coalesce(:text, null) is null or (lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%')))) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:paid, null) is null or e.paid = :paid) " +
            "and e.eventDate >= :rangeStart " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) " +
            "and (:onlyAvailable = false or e.id in " +
            "(select r.event.id " +
            "from Request r " +
            "where r.status = 'CONFIRMED' " +
            "group by r.event.id " +
            "having e.participantLimit - count(id) > 0 " +
            "order by count(r.id))) ")
    List<Event> findAllPublic(@Param("text") String text, @Param("categoryIds") Set<Long> categoryIds,
                              @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable,
                              Pageable pageable);

    @Query("SELECT MIN(e.publishedOn) FROM Event e WHERE e.id IN :eventsId")
    Optional<LocalDateTime> getStart(@Param("eventsId") Collection<Long> eventsId);
}
