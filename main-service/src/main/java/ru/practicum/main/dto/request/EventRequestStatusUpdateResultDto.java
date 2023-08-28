package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventRequestStatusUpdateResultDto {
    @NotNull
    private List<ParticipationRequestDto> confirmedRequests;

    @NotNull
    private List<ParticipationRequestDto> rejectedRequests;
}
