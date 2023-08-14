package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.enums.EventState;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.main.util.Formats.DATE_PATTERN;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    String annotation;
    CategoryDto category;
    Long confirmedRequests;

    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;

    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime publishedOn;

    Boolean requestModeration;
    EventState state;
    String title;
    Long views;

}
