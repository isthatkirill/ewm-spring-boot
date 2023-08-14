package ru.practicum.main.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {

    List<Long> events;
    Boolean pinned;

    @Size(min = 1, max = 50, message = "Title must be from 1 to 50 characters")
    String title;

}
