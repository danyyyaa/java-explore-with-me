package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aspect.ToLog;
import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/comments/{userId}")
@RequiredArgsConstructor
@Validated
@ToLog
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullCommentDto saveComment(@Positive @PathVariable Long userId,
                                      @Positive @RequestParam Long eventId,
                                      @Valid @RequestBody NewCommentDto dto) {
        return commentService.saveComment(userId, dto, eventId);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAddedCurrentUser(@Positive @RequestParam Long commentId,
                                              @Positive @PathVariable Long userId) {
        commentService.deleteCommentAddedCurrentUser(commentId, userId);
    }

    @PatchMapping
    public FullCommentDto updateCommentByAuthor(@Positive @RequestParam Long commentId,
                                                @Positive @PathVariable Long userId,
                                                @Valid @RequestBody UpdateCommentDto dto) {
        return commentService.updateCommentByAuthor(commentId, userId, dto);
    }
}
