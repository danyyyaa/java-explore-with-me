package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.dto.user.NewUserRequestDto;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(NewUserRequestDto newUserRequestDto);
    UserDto toResponseDto(User user);
    UserShortDto toInitiator(User user);
}
