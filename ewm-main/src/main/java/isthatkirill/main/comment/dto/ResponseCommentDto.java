package isthatkirill.main.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ResponseCommentDto {

    Long id;
    String message;
    Long authorId;
    Long eventId;

    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime created;

}
