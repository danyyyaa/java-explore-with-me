package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Comment;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c" +
            " from Comment c" +
            " where c.event.id = :eventId")
    List<Comment> getCommentsByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT c.event.id AS eventId, COUNT(c.id) AS commentCount " +
            "FROM Comment c " +
            "WHERE c.event.id IN :eventIds " +
            "GROUP BY c.event.id")
    List<Map<Long, Long>> countCommentsByEventIdsIn(@Param("eventIds") Collection<Long> eventIds);
}
