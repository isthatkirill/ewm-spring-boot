package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentFullDto;
import ru.practicum.main.comment.dto.NewCommentDto;

public interface CommentService {

    CommentFullDto create(NewCommentDto newComment, Long userId, Long eventId);

}
