package isthatkirill.main.compilation.dto;

import isthatkirill.main.validation.group.OnCreate;
import isthatkirill.main.validation.group.OnUpdate;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationRequestDto {

    List<Long> events = new ArrayList<>();
    Boolean pinned;

    @NotBlank(message = "Title cannot be null or blank", groups = OnCreate.class)
    @Size(min = 1, max = 50, message = "Title must be from 1 to 50 characters", groups = {OnCreate.class, OnUpdate.class})
    String title;

}
