package isthatkirill.main.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import isthatkirill.main.request.model.RequestState;
import isthatkirill.main.util.Formats;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    @JsonFormat(pattern = Formats.DATE_PATTERN)
    LocalDateTime created;

    Long event;
    Long id;
    Long requester;
    RequestState status;

}
