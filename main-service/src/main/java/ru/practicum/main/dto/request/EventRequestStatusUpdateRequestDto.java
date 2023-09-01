package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.entity.enums.EventRequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventRequestStatusUpdateRequestDto {
    private Set<Long> requestIds;
    private EventRequestStatus status;
}

