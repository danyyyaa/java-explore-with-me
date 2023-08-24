package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHit saveEndpointHit(EndpointHit endpointHit) {
        return endpointHitRepository.save(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ViewStatsResponseDto> getVisitStats(LocalDateTime start, LocalDateTime end,
                                                          Set<String> uris, boolean unique) {

        return unique
                ? endpointHitRepository.getAllHitsByTimestampAndUriUnique(uris, start, end)
                : endpointHitRepository.getAllByTimestampAndUriNotUnique(uris, start, end);
    }
}

