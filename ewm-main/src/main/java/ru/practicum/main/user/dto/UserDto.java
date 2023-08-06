package ru.practicum.main.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    @NotBlank(message = "Name cannot be blank")
    String name;

    @Email(message = "Email must satisfy pattern")
    @NotNull(message = "Email cannot be null")
    String email;

}
