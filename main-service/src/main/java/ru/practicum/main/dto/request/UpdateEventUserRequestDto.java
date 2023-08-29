package ru.practicum.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.entity.enums.StateAction;

import java.time.LocalDateTime;

import static ru.practicum.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateEventUserRequestDto {

    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;

    private LocationDtoCoordinates location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Length(min = 3, max = 120)
    private String title;
}
