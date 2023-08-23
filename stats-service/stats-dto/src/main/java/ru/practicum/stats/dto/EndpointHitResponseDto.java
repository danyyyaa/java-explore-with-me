package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.stats.util.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EndpointHitResponseDto {

    private Long id;

    @Size(max = 255)
    private String app;

    @Size(max = 255)
    private String uri;

    @Size(max = 255)
    private String ip;

    @JsonFormat(pattern = TIME_PATTERN)
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime timestamp;
}
