package ru.practicum.main.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.validation.group.OnCreate;
import ru.practicum.main.validation.group.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotBlank(message = "Message cannot be blank or null", groups = OnCreate.class)
    @Size(min = 2, max = 1024, message = "Message must be from 20 to 7000 characters",
            groups = {OnCreate.class, OnUpdate.class})
    String message;

}
