package ru.practicum.stats.service;

import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    EndpointHit saveEndpointHit(EndpointHit endpointHit);

    ViewStatsResponseDto getVisitStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique);

}
