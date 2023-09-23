package isthatkirill.main.comment.repository;

import isthatkirill.main.comment.model.Comment;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> findAllCommentsForEvent(Long eventId, String keyword, Integer from, Integer size);

}
