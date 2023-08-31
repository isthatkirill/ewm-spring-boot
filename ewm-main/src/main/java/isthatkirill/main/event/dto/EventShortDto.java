package isthatkirill.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.user.dto.UserShortDto;
import isthatkirill.main.util.Formats;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class EventShortDto {

    String annotation;
    CategoryDto category;
    Long confirmedRequests;

    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime eventDate;

    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;

}
