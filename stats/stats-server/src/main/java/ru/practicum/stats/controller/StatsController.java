package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.service.EndpointHitService;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {

    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    public EndpointHitResponseDto saveHit(@Valid @RequestBody EndpointHitRequestDto endpointHitRequestDto) {
         EndpointHit endpointHit = endpointHitService.saveEndpointHit(
                 EndpointHitMapper.INSTANCE.toEndpointHit(endpointHitRequestDto));

         return EndpointHitMapper.INSTANCE.toEndpointHitResponseDto(endpointHit);
    }

    @GetMapping("/stats")
    public ViewStatsDto getStats() {
        return null;
    }
}
