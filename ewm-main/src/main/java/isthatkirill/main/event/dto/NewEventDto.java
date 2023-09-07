package isthatkirill.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import isthatkirill.main.event.validator.annotation.ValidEventDate;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.util.Formats;
import isthatkirill.main.validation.group.OnCreate;
import isthatkirill.main.validation.group.OnUpdate;
import isthatkirill.main.validation.group.OnUpdateAdmin;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class NewEventDto {

    @NotBlank(message = "Annotation cannot be blank", groups = OnCreate.class)
    @Size(min = 20, max = 2000, message = "The event annotation must be from 20 to 2000 characters",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    String annotation;

    @NotNull(message = "Category cannot be null", groups = OnCreate.class)
    Long category;

    @NotBlank(message = "Description cannot be blank", groups = OnCreate.class)
    @Size(min = 20, max = 7000, message = "The event description must be from 20 to 7000 characters",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    String description;

    @NotNull(message = "Event date cannot be null", groups = OnCreate.class)
    @JsonFormat(pattern = Formats.DATE_PATTERN)
    @ValidEventDate(isAdmin = false, groups = {OnCreate.class, OnUpdate.class})
    @ValidEventDate(isAdmin = true, groups = {OnUpdateAdmin.class})
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero(message = "ParticipantLimit cannot be negative", groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Title cannot be blank or null", groups = OnCreate.class)
    @Size(min = 3, max = 120, message = "The event title must be from 3 to 120 characters",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    String title;

}

