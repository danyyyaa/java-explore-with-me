package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.entity.enums.EventPublishedStatus;

import java.time.LocalDateTime;

import static ru.practicum.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDtoCoordinates location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventPublishedStatus published;

    private String title;

    private Long views;
}
