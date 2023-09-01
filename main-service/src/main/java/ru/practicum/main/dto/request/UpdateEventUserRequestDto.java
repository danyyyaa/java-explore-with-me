package ru.practicum.main.dto.request;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateEventUserRequestDto extends UpdateEventRequest {
    private StateAction stateAction;
    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
