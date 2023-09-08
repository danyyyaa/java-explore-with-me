package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.entity.Comment;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mapper.CommentMapper;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public FullCommentDto saveComment(Long userId, NewCommentDto newCommentDto, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId)));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException(String.format("Event %s isn't published", eventId));
        }

        Comment comment = Comment.builder()
                .content(newCommentDto.getContent())
                .created(LocalDateTime.now())
                .event(event)
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toFullCommentDto(savedComment);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        boolean isExist = commentRepository.existsById(commentId);

        if (isExist) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException(String.format("Comment %s not found", commentId));
        }
    }

    @Override
    public void deleteCommentAddedCurrentUser(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment %s not found", commentId)));

        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", authorId)));

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<FullCommentDto> getCommentsByEventId(Long eventId, Pageable pageable) {
        List<Comment> comments = commentRepository.getCommentsByEventId(eventId, pageable);

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(commentMapper::toFullCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public FullCommentDto updateCommentByAuthor(Long commentId, Long authorId, UpdateCommentDto dto) {
        Comment toUpdateComment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment %s not found", commentId)));

        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", authorId)));

        if (!toUpdateComment.getAuthor().getId().equals(author.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId));
        }

        toUpdateComment.setContent(dto.getContent());
        toUpdateComment.setUpdated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(toUpdateComment);

        return commentMapper.toFullCommentDto(savedComment);
    }
}
