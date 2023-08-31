package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.aspect.ToLog;
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

import static ru.practicum.Constant.TIME_PATTERN;

@RestController
@RequestMapping
@RequiredArgsConstructor
@ToLog
public class StatsController {

    private final EndpointHitService endpointHitService;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
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
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End cannot be earlier that start");
        }
        return endpointHitService.getVisitStats(start, end, uris, unique);
    }
}
