package isthatkirill.main.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCommentDto {

    @NotBlank(message = "Message cannot be blank or null")
    @Size(min = 2, max = 1024, message = "Message must be from 20 to 1024 characters")
    String message;

}
