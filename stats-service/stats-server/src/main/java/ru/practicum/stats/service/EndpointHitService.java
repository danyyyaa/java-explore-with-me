package ru.practicum.stats.service;

import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EndpointHitService {

    EndpointHit saveEndpointHit(EndpointHit endpointHit);

    Collection<ViewStatsResponseDto> getVisitStats(LocalDateTime start, LocalDateTime end,
                                                   Set<String> uris, boolean unique);

}
