package ru.practicum.main.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    List<Long> events = new ArrayList<>();
    Boolean pinned = false;

    @NotBlank(message = "Title cannot be null or blank")
    @Size(min = 1, max = 50, message = "Title must be from 1 to 50 characters")
    String title;

}
