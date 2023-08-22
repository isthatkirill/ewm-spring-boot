package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.comment.dto.RequestCommentDto;
import ru.practicum.main.comment.dto.ResponseCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public ResponseCommentDto create(RequestCommentDto newComment, Long userId, Long eventId) {
        log.info("Add new comment by user id={} for event id={} --> {}", userId, eventId, newComment);

        User user = checkIfUserExistsAndGet(userId);
        Event event = checkIfEventExistsAndGet(eventId);
        checkIfPublished(event);

        Comment comment = commentRepository.save(commentMapper.toComment(newComment, user, event));
        return commentMapper.toResponseCommentDto(comment);
    }

    @Override
    public ResponseCommentDto update(RequestCommentDto newComment, Long userId, Long commentId) {
        log.info("Update comment id={} by user id={} --> {}", commentId, userId, newComment);

        checkIfUserExists(userId);
        Comment comment = checkIfOwnCommentExistsAndGet(userId, commentId);
        comment.setMessage(newComment.getMessage());

        return commentMapper.toResponseCommentDto(commentRepository.save(comment));
    }

    private Comment checkIfOwnCommentExistsAndGet(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(Comment.class, commentId));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Cannot update not own comment");
        }
        return comment;
    }

    private User checkIfUserExistsAndGet(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    private Event checkIfEventExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
    }

    private void checkIfPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Cannot create a comment for an unpublished event");
        }
    }

}
