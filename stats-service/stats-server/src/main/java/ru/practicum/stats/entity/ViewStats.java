package ru.practicum.stats.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class ViewStats {
    private String app;
    private String uri;
    private String hits;
}
