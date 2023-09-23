package isthatkirill.main.comment.repository;

import isthatkirill.main.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    List<Comment> getCommentsByAuthorId(Long userId);

}
