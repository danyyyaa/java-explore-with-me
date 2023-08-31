package ru.practicum.main.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewCompilationDto {

    private Set<Long> events;

    private boolean pinned;

    @Length(min = 1, max = 50)
    @NotBlank
    private String title;
}
