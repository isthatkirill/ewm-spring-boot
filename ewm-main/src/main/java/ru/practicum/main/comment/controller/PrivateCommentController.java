package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentFullDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.service.CommentService;
import ru.practicum.main.validation.group.OnCreate;

@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto create(@RequestBody @Validated(OnCreate.class) NewCommentDto newComment,
                                 @PathVariable Long userId, @PathVariable Long eventId) {
        return commentService.create(newComment, userId, eventId);
    }


}
