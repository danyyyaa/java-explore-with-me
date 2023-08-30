package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitRequestDto;
import ru.practicum.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.practicum.Constant.TIME_PATTERN;

@Service
public class StatsClient {

    @Value("${stats-server.url}")
    private String serverUrl;

    private final RestTemplate restTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Autowired
    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createHit(EndpointHitRequestDto endpointHitRequestDto) {
        restTemplate.postForLocation(serverUrl.concat("/hit"), endpointHitRequestDto);
    }

    public List<ViewStatsResponseDto> getStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "unique", unique));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }

        ViewStatsResponseDto[] response = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                ViewStatsResponseDto[].class, parameters);

        return Objects.isNull(response)
                ? List.of()
                : List.of(response);
    }
}
