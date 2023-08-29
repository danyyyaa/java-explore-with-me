package ru.practicum.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.entity.enums.EventRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private EventRequestStatus status;
}
