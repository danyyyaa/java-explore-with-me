package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static ru.practicum.stats.util.Constant.TIME_PATTERN;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {

    private final EndpointHitService endpointHitService;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("/hit")
    public EndpointHitResponseDto saveHit(@Valid @RequestBody EndpointHitRequestDto endpointHitRequestDto) {
        EndpointHit endpointHit = endpointHitService.saveEndpointHit(
                endpointHitMapper.toEndpointHit(endpointHitRequestDto));

        return endpointHitMapper.toEndpointHitResponseDto(endpointHit);
    }

    @GetMapping("/stats")
    public Collection<ViewStatsResponseDto> getStats(@RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime start,
                                                     @RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime end,
                                                     @RequestParam(required = false) Set<String> uris,
                                                     @RequestParam(defaultValue = "false") boolean unique) {
        return endpointHitService.getVisitStats(start, end, uris, unique);
    }
}
