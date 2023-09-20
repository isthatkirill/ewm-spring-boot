package isthatkirill.main.comment.service;

import isthatkirill.main.comment.dto.RequestCommentDto;
import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.event.repository.EventRepository;
import isthatkirill.main.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Kirill Emelyanov
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    //currentCommentId = 12 --> test-events.sql contains previous 11 events

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-comments.sql"})
    void createTest() {
        Long eventId = 8L;
        Long userId = 1L;
        Long commentId = 12L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Is html a programming language?").build();

        ResponseCommentDto responseCommentDto = commentService.create(requestCommentDto, userId, eventId);

        assertThat(responseCommentDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("message", requestCommentDto.getMessage())
                .hasFieldOrPropertyWithValue("authorId", userId)
                .hasFieldOrPropertyWithValue("eventId", eventId)
                .hasFieldOrProperty("created");
    }

    @Test
    @Order(2)
    void createByNonExistentUserTest() {
        Long eventId = 8L;
        Long userId = 999L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Is html a programming language?").build();

        assertThrows(EntityNotFoundException.class, () -> commentService.create(requestCommentDto, userId, eventId));
    }

    @Test
    @Order(3)
    void createOnNonExistentEventTest() {
        Long eventId = 999L;
        Long userId = 1L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Is html a programming language?").build();

        assertThrows(EntityNotFoundException.class, () -> commentService.create(requestCommentDto, userId, eventId));
    }

    @Test
    @Order(4)
    void createOnNonPublishedEventTest() {
        Long eventId = 3L;
        Long userId = 1L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Is html a programming language?").build();

        assertThrows(ForbiddenException.class, () -> commentService.create(requestCommentDto, userId, eventId));
    }

    @Test
    @Order(5)
    void updateTest() {
        Long commentId = 12L;
        Long eventId = 8L;
        Long userId = 1L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Sorry for the stupid question").build();

        ResponseCommentDto responseCommentDto = commentService.update(requestCommentDto, userId, commentId);

        assertThat(responseCommentDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("message", requestCommentDto.getMessage())
                .hasFieldOrPropertyWithValue("authorId", userId)
                .hasFieldOrPropertyWithValue("eventId", eventId)
                .hasFieldOrProperty("created");
    }

    @Test
    @Order(6)
    void updateByNonExistentUserTest() {
        Long commentId = 12L;
        Long userId = 999L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Sorry for the stupid question").build();

        assertThrows(EntityNotFoundException.class, () -> commentService.update(requestCommentDto, userId, commentId));
    }

    @Test
    @Order(7)
    void updateNotOwnCommentTest() {
        Long eventId = 12L;
        Long userId = 2L;
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("Sorry for the stupid question").build();

        assertThrows(ForbiddenException.class, () -> commentService.update(requestCommentDto, userId, eventId));
    }

    @Test
    @Order(8)
    void getByIdByUserTest() {
        Long commentId = 1L;
        Long userId = 5L;

        ResponseCommentDto responseCommentDto = commentService.getByIdByUser(userId, commentId);

        assertThat(responseCommentDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("message", "cannot wait for it")
                .hasFieldOrPropertyWithValue("authorId", userId)
                .hasFieldOrPropertyWithValue("eventId", 2L)
                .hasFieldOrProperty("created");
    }

    @Test
    @Order(9)
    void getByIdByUserNotOwnCommentTest() {
        Long commentId = 1L;
        Long userId = 2L;

        assertThrows(ForbiddenException.class, () -> commentService.getByIdByUser(userId, commentId));
    }

    @Test
    @Order(10)
    void getByIdByNonExistentUserTest() {
        Long commentId = 1L;
        Long userId = 999L;

        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByUser(userId, commentId));
    }

    @Test
    @Order(11)
    void getUsersCommentsTest() {
        Long userId = 3L;

        List<ResponseCommentDto> comments = commentService.getUsersComments(userId);

        assertThat(comments).hasSize(2)
                .extracting(ResponseCommentDto::getMessage)
                .containsExactlyInAnyOrder("i will definitely come, c++ is my favourite programming language", "nice!");
    }

    @Test
    @Order(12)
    void getUsersCommentsByNonExistentUserTest() {
        Long userId = 999L;

        assertThrows(EntityNotFoundException.class, () -> commentService.getUsersComments(userId));
    }

    @Test
    @Order(13)
    void getByIdByAdminTest() {
        Long commentId = 4L;

        ResponseCommentDto responseCommentDto = commentService.getByIdByAdmin(commentId);

        assertThat(responseCommentDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("message", "will we talking about c?")
                .hasFieldOrPropertyWithValue("authorId", 4L)
                .hasFieldOrPropertyWithValue("eventId", 2L)
                .hasFieldOrProperty("created");
    }

    @Test
    @Order(14)
    void getByIdByAdminNonExistentCommentTest() {
        Long commentId = 999L;

        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByAdmin(commentId));
    }

    @Test
    @Order(15)
    void deleteByIdByAdminTest() {
        Long commentId = 10L;

        assertDoesNotThrow(() -> commentService.deleteByIdByAdmin(commentId));
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteByIdByAdmin(commentId));
    }

    @Test
    @Order(16)
    void deleteByIdByUserTest() {
        Long commentId = 11L;
        Long userId = 3L;

        assertDoesNotThrow(() -> commentService.deleteByIdByUser(userId, commentId));
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteByIdByUser(userId, commentId));
    }

    @Test
    @Order(17)
    void deleteByIdByUserNotOwnCommentTest() {
        Long commentId = 2L;
        Long userId = 3L;

        assertThrows(ForbiddenException.class, () -> commentService.deleteByIdByUser(userId, commentId));
    }

    @Test
    @Order(18)
    void getAllCommentsForEventTest() {
        Long eventId = 2L;
        String keyword = "is my";
        Integer size = 10;
        Integer from = 0;

        List<ResponseCommentDto> comments = commentService.getAllCommentsForEvent(eventId, keyword, from, size);

        assertThat(comments).hasSize(2)
                .extracting(ResponseCommentDto::getMessage)
                .containsExactlyInAnyOrder("c++ is my fav lang", "i will definitely come, c++ is my favourite programming language");
    }

    @Test
    @Order(19)
    void cascadeCommentDeletingWhenDeleteUserTest() {
        Long userId = 3L;
        Long commentId = 3L;

        assertDoesNotThrow(() -> userRepository.deleteById(userId));
        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByAdmin(commentId));
    }

    @Test
    @Order(20)
    void cascadeCommentDeletingWhenDeleteEventTest() {
        Long eventId = 6L;
        Long commentOneId = 6L;
        Long commentTwoId = 7L;
        Long commentThreeId = 8L;

        assertDoesNotThrow(() -> eventRepository.deleteById(eventId));
        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByAdmin(commentOneId));
        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByAdmin(commentTwoId));
        assertThrows(EntityNotFoundException.class, () -> commentService.getByIdByAdmin(commentThreeId));
    }
}