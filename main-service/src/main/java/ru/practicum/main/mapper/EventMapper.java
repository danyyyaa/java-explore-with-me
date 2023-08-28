package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.entity.Category;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.Location;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventPublishedStatus;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    //@Mapping(target = "createdOn", expression = "java(setLocalDateTimeNow())")
    Event toEvent(NewEventDto newEventDto, Location location, Category category,
                  EventPublishedStatus state, User initiator);

    EventShortDto toEventShortDto(Event event);


    EventFullDto toEventFullDto(Event event, Category category, User initiator);
    EventFullDto toEventFullDto(Event event);

    /*default LocalDateTime setLocalDateTimeNow() {
        return LocalDateTime.now();
    }*/

}
