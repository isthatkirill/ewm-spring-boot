package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.event.model.enums.EventStateAction;
import ru.practicum.main.event.validator.annotation.ValidEventDate;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.main.util.Formats.DATE_PATTERN;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "The event annotation must be from 20 to 2000 characters")
    String annotation;

    Long category;

    @Size(min = 20, max = 7000, message = "The event description must be from 20 to 7000 characters")
    String description;

    @ValidEventDate
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero(message = "ParticipantLimit cannot be negative")
    Integer participantLimit;

    Boolean requestModeration;
    EventStateAction stateAction;

    @Size(min = 3, max = 120, message = "The event title must be from 3 to 120 characters")
    String title;

}
