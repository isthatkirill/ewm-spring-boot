package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.RequestCommentDto;
import ru.practicum.main.comment.dto.ResponseCommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDto create(@RequestBody @Valid RequestCommentDto newComment,
                                     @PathVariable Long userId, @PathVariable Long eventId) {
        return commentService.create(newComment, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public ResponseCommentDto update(@RequestBody @Valid RequestCommentDto newComment,
                                     @PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.update(newComment, userId, commentId);
    }


}
