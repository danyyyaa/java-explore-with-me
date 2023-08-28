package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.dto.location.LocationFullDto;
import ru.practicum.main.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toLocation(LocationDtoCoordinates locationDtoCoordinates);
    LocationFullDto toLocationFullDto(Location location);
    LocationDtoCoordinates toLocationDtoCoordinates(Location location);

}
