package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c" +
            " from Comment c" +
            " where c.event.id = :eventId")
    List<Comment> getCommentsByEventId(@Param("eventId") Long eventId, Pageable pageable);
}
