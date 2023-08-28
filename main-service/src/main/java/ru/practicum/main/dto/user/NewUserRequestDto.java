package ru.practicum.main.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewUserRequestDto {

    @NotBlank
    @Length(min = 6, max = 254)
    private String name;

    @NotBlank
    @Email
    @Length(min = 2, max = 250)
    private String email;
}
