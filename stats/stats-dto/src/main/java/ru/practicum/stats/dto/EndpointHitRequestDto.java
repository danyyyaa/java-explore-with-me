package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class EndpointHitRequestDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
