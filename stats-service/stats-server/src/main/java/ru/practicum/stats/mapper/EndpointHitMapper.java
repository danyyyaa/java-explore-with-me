package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.entity.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHit toEndpointHit(EndpointHitRequestDto endpointHitRequestDto);

    EndpointHitResponseDto toEndpointHitResponseDto(EndpointHit endpointHit);
}
