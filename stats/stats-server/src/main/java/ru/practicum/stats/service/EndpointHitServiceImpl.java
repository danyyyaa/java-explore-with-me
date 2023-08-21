package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.repository.EndpointHitRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHit saveEndpointHit(EndpointHit endpointHit) {
        return endpointHitRepository.save(endpointHit);
    }

}
