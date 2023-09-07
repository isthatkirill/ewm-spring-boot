package isthatkirill.main.location.dto;

import isthatkirill.main.validation.group.OnCreate;
import isthatkirill.main.validation.group.OnUpdate;
import isthatkirill.main.validation.group.OnUpdateAdmin;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {

    @NotNull
    Float lat;

    @NotNull
    Float lon;

}
