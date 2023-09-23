package isthatkirill.main.comment.controller;

import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.comment.service.CommentServiceImpl;
import isthatkirill.main.util.Formats;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = AdminCommentController.class)
class AdminCommentControllerTest {

    @MockBean
    private CommentServiceImpl commentService;

    @Autowired
    private MockMvc mvc;

    private final ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
            .id(1L)
            .authorId(1L)
            .message("comment message")
            .created(LocalDateTime.now().minusDays(1))
            .build();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Formats.DATE_PATTERN);
    private final Long commentId = 1L;

    @Test
    @SneakyThrows
    void getByIdByAdminTest() {
        when(commentService.getByIdByAdmin(anyLong())).thenReturn(responseCommentDto);

        mvc.perform(get("/admin/comments/{commentId}", commentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$.message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$.created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).getByIdByAdmin(commentId);
    }

    @Test
    @SneakyThrows
    void deleteByIdByAdmin() {
        mvc.perform(delete("/admin/comments/{commentId}", commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteByIdByAdmin(commentId);
    }

}