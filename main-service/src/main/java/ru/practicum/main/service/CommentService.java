package ru.practicum.main.service;

import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {
    FullCommentDto saveComment(Long userId, NewCommentDto newCommentDto, Long eventId);

    void deleteCommentByAdmin(Long commentId);

    void deleteCommentAddedCurrentUser(Long commentId, Long authorId);

    Collection<FullCommentDto> getCommentsByEventId(Long eventId);

    FullCommentDto updateCommentByAuthor(Long commentId, Long authorId, UpdateCommentDto dto);
}
