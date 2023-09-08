package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import ru.practicum.main.dto.comment.FullCommentDto;
import ru.practicum.main.entity.Comment;

@Mapper(componentModel = "spring", uses = {UserMapper.class},
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface CommentMapper {
    FullCommentDto toFullCommentDto(Comment comment);
}
