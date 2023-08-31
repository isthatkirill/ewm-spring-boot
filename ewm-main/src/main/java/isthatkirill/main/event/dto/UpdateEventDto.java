package isthatkirill.main.event.dto;

import isthatkirill.main.event.model.EventStateAction;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventDto extends NewEventDto {

    EventStateAction stateAction;

}
