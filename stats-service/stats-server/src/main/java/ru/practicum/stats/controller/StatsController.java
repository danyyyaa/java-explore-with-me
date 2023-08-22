package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.aspect.ToLog;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.EndpointHitResponseDto;
import ru.practicum.stats.dto.ViewStatsResponseDto;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.util.Constant.TIME_PATTERN;

@RestController
@RequestMapping
@RequiredArgsConstructor
@ToLog
public class StatsController {

    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    public EndpointHitResponseDto saveHit(@Valid @RequestBody EndpointHitRequestDto endpointHitRequestDto) {
        EndpointHit endpointHit = endpointHitService.saveEndpointHit(
                EndpointHitMapper.INSTANCE.toEndpointHit(endpointHitRequestDto));

        return EndpointHitMapper.INSTANCE.toEndpointHitResponseDto(endpointHit);
    }

    @GetMapping("/stats")
    public ViewStatsResponseDto getStats(@RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") boolean unique) {
        return endpointHitService.getVisitStats(start, end, uris, unique);
    }
}
