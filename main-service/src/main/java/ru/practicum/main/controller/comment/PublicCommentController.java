package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.aspect.ToLog;
import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@ToLog
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public Collection<FullCommentDto> getCommentsByEventId(@Positive @RequestParam Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }
}
