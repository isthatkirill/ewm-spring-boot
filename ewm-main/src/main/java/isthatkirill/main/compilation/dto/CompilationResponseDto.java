package isthatkirill.main.compilation.dto;

import isthatkirill.main.event.dto.EventShortDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponseDto {

    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    String title;

}
