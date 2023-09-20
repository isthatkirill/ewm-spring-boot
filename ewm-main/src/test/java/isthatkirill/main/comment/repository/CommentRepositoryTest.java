package isthatkirill.main.comment.repository;

import isthatkirill.main.comment.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-comments.sql"})
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void getCommentsByAuthorIdTest() {
        Long userId = 5L;

        List<Comment> comments = commentRepository.getCommentsByAuthorId(userId);

        assertThat(comments).hasSize(2)
                .extracting(Comment::getMessage)
                .containsExactlyInAnyOrder("cannot wait for it", "what is the cover charge?");
    }

    @Test
    void getCommentsByNonExistentAuthorIdTest() {
        Long userId = 999L;

        List<Comment> comments = commentRepository.getCommentsByAuthorId(userId);

        assertThat(comments).isEmpty();
    }

    @Test
    void findAllCommentsForEventTest() {
        Long eventId = 2L;
        String keyword = "is my";
        Integer size = 10;
        Integer from = 0;

        List<Comment> comments = commentRepository.findAllCommentsForEvent(eventId, keyword, from, size);

        assertThat(comments).hasSize(2)
                .extracting(Comment::getMessage)
                .containsExactlyInAnyOrder("c++ is my fav lang", "i will definitely come, c++ is my favourite programming language");
    }

    @Test
    void findAllCommentsForEventWithPaginationWithoutKeywordTest() {
        Long eventId = 2L;
        Integer size = 2;
        Integer from = 2;

        List<Comment> comments = commentRepository.findAllCommentsForEvent(eventId, null, size, from);

        assertThat(comments).hasSize(2)
                .extracting(Comment::getMessage)
                .containsExactlyInAnyOrder("will we talking about c?", "i will definitely come, c++ is my favourite programming language");
    }

    @Test
    void findAllCommentsForNonExistentEventTest() {
        Long eventId = 999L;
        Integer size = 0;
        Integer from = 10;

        List<Comment> comments = commentRepository.findAllCommentsForEvent(eventId, null, size, from);

        assertThat(comments).isEmpty();
    }

}