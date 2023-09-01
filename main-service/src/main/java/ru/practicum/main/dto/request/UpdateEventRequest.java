package ru.practicum.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.validation.EventDateValidator;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    @EventDateValidator
    private LocalDateTime eventDate;

    @Valid
    private LocationDtoCoordinates location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    //private StateAction stateAction;

    @Length(min = 3, max = 120)
    private String title;
}
