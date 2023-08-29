package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.dto.location.LocationDtoCoordinates;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private LocationDtoCoordinates location;

    private boolean paid;

    @NotNull
    private long participantLimit;

    @NotNull
    private Boolean requestModeration;

    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}
