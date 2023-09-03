package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.enums.RequestStatus;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "status", expression = "java(mapEventRequestStatus(request))")
    ParticipationRequestDto toParticipationRequestDto(Request request);

    default RequestStatus mapEventRequestStatus(Request request) {
        if (request == null || request.getStatus() == null) {
            return null;
        }
        return RequestStatus.valueOf(request.getStatus().name());
    }
}