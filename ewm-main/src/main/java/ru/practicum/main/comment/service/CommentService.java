package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.RequestCommentDto;
import ru.practicum.main.comment.dto.ResponseCommentDto;

public interface CommentService {

    ResponseCommentDto create(RequestCommentDto newComment, Long userId, Long eventId);

    ResponseCommentDto update(RequestCommentDto newComment, Long userId, Long commentId);

}
