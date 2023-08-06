package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.stats.util.Formats;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {

    Long id;

    @Size(max = 255, message = "App length cannot be more than 255")
    @NotBlank(message = "App cannot be blank")
    String app;

    @Size(max = 255, message = "Uri length cannot be more than 255")
    @NotBlank(message = "Uri cannot be blank")
    String uri;

    @Size(max = 31, message = "Ip length cannot be more than 31")
    @NotBlank(message = "Ip cannot be blank")
    String ip;

    @Past(message = "Timestamp cannot be in future")
    @NotNull(message = "Timestamp cannot be null")
    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime timestamp;

}
