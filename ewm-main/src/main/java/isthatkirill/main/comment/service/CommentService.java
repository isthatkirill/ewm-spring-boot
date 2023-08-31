package isthatkirill.main.comment.service;

import isthatkirill.main.comment.dto.RequestCommentDto;
import isthatkirill.main.comment.dto.ResponseCommentDto;

import java.util.List;

public interface CommentService {

    ResponseCommentDto create(RequestCommentDto newComment, Long userId, Long eventId);

    ResponseCommentDto update(RequestCommentDto newComment, Long userId, Long commentId);

    ResponseCommentDto getByIdByUser(Long userId, Long commentId);

    List<ResponseCommentDto> getUsersComments(Long userId);

    void deleteByIdByUser(Long userId, Long commentId);

    ResponseCommentDto getByIdByAdmin(Long commentId);

    void deleteByIdByAdmin(Long commentId);

    List<ResponseCommentDto> getAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size);

}
