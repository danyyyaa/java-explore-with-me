package ru.practicum.main.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LocationDtoCoordinates {
    @NotNull
    @PositiveOrZero
    private Float lat;

    @NotNull
    @PositiveOrZero
    private Float lon;
}
