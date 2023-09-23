package isthatkirill.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.util.Formats;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto extends EventShortDto {

    String description;
    LocationDto location;
    Integer participantLimit;
    Boolean requestModeration;
    EventState state;

    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime publishedOn;

    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime createdOn;

}
