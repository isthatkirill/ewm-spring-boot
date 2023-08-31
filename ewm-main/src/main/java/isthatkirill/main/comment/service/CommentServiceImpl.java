package isthatkirill.main.comment.service;

import isthatkirill.main.comment.dto.RequestCommentDto;
import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.comment.mapper.CommentMapper;
import isthatkirill.main.comment.model.Comment;
import isthatkirill.main.comment.repository.CommentRepository;
import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.event.model.Event;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.repository.EventRepository;
import isthatkirill.main.user.model.User;
import isthatkirill.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ResponseCommentDto create(RequestCommentDto newComment, Long userId, Long eventId) {
        log.info("Add new comment by user id={} for event id={} --> {}", userId, eventId, newComment);

        User user = checkIfUserExistsAndGet(userId);
        Event event = checkIfEventExistsAndGet(eventId);
        checkIfPublished(event);

        Comment comment = commentRepository.save(commentMapper.toComment(newComment, user, event));
        return commentMapper.toResponseCommentDto(comment);
    }

    @Override
    @Transactional
    public ResponseCommentDto update(RequestCommentDto newComment, Long userId, Long commentId) {
        log.info("Update comment id={} by user id={} --> {}", commentId, userId, newComment);

        checkIfUserExists(userId);
        Comment comment = checkIfOwnCommentExistsAndGet(userId, commentId);
        comment.setMessage(newComment.getMessage());

        return commentMapper.toResponseCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseCommentDto getByIdByUser(Long userId, Long commentId) {
        log.info("User id={} requested his own comment id={}", userId, commentId);
        return commentMapper.toResponseCommentDto(checkIfOwnCommentExistsAndGet(userId, commentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getUsersComments(Long userId) {
        log.info("User id={} requested all his comments", userId);
        return commentMapper.toResponseCommentDto(commentRepository.getCommentsByAuthorId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseCommentDto getByIdByAdmin(Long commentId) {
        log.info("Get comment id={} by admin", commentId);
        return commentMapper.toResponseCommentDto(checkIfCommentExistsAndGet(commentId));
    }


    @Override
    @Transactional
    public void deleteByIdByAdmin(Long commentId) {
        log.info("Delete comment id={} by admin", commentId);
        checkIfCommentExists(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size) {
        log.info("Requested all comments for event id={} with filter params keyword={},  from={}, size={}",
                eventId, keyword, from, size);
        return commentMapper.toResponseCommentDto(commentRepository
                .findAllCommentsForEvent(eventId, keyword, from, size));
    }

    @Override
    @Transactional
    public void deleteByIdByUser(Long userId, Long commentId) {
        checkIfOwnCommentExistsAndGet(userId, commentId);
        log.info("Delete comment id={} by user id={}", commentId, userId);
        commentRepository.deleteById(commentId);
    }

    private Comment checkIfOwnCommentExistsAndGet(Long userId, Long commentId) {
        Comment comment = checkIfCommentExistsAndGet(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Cannot operate with not own comment");
        }
        return comment;
    }

    private Comment checkIfCommentExistsAndGet(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(Comment.class, commentId));
    }

    private void checkIfCommentExists(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(Comment.class, commentId);
        }
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
