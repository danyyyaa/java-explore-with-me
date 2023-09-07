package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.aspect.ToLog;
import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.service.CommentService;
import ru.practicum.main.util.OffsetBasedPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@ToLog
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public Collection<FullCommentDto> getCommentsByEventId(@Positive @RequestParam Long eventId,
                                                           @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return commentService.getCommentsByEventId(eventId, page);
    }
}
