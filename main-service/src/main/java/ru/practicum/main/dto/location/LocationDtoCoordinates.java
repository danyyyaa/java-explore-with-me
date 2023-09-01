package ru.practicum.main.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LocationDtoCoordinates {
    @Min(-90)
    @Max(90)
    private Float lat;

    @Min(-180)
    @Max(180)
    private Float lon;
}
