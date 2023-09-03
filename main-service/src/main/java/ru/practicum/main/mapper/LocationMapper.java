package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toLocation(LocationDtoCoordinates locationDtoCoordinates);

    LocationDtoCoordinates toLocationDtoCoordinates(Location location);
}
