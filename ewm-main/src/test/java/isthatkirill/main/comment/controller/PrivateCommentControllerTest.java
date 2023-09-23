package isthatkirill.main.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.comment.dto.RequestCommentDto;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = PrivateCommentController.class)
class PrivateCommentControllerTest {

    @MockBean
    private CommentServiceImpl commentService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
            .id(1L)
            .authorId(1L)
            .message("comment message")
            .created(LocalDateTime.now().minusDays(1))
            .build();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Formats.DATE_PATTERN);
    private final Long userId = 1L;
    private final Long commentId = 1L;
    private final Long eventId = 1L;

    @Test
    @SneakyThrows
    void createTest() {
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("comment message").build();

        when(commentService.create(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(post("/users/{userId}/comments/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$.message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$.created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).create(requestCommentDto, userId, eventId);
    }

    @Test
    @SneakyThrows
    void createTooShortCommentTest() {
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("x").build();

        when(commentService.create(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(post("/users/{userId}/comments/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).create(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void createEmptyCommentTest() {
        when(commentService.create(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(post("/users/{userId}/comments/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RequestCommentDto()))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).create(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("comment message").build();

        when(commentService.update(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(patch("/users/{userId}/comments/{commentId}", commentId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$.message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$.created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).update(requestCommentDto, commentId, eventId);
    }

    @Test
    @SneakyThrows
    void updateWithTooShortCommentTest() {
        RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("x").build();

        when(commentService.update(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(patch("/users/{userId}/comments/{commentId}", commentId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).update(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateWithEmptyCommentTest() {
        when(commentService.update(any(), anyLong(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(patch("/users/{userId}/comments/{commentId}", commentId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RequestCommentDto()))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).update(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getByIdByUserTest() {
        when(commentService.getByIdByUser(anyLong(), anyLong())).thenReturn(responseCommentDto);

        mvc.perform(get("/users/{userId}/comments/{commentId}", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$.message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$.created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).getByIdByUser(userId, commentId);
    }

    @Test
    @SneakyThrows
    void getUsersCommentsTest() {
        when(commentService.getUsersComments(anyLong())).thenReturn(List.of(responseCommentDto));

        mvc.perform(get("/users/{userId}/comments", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$[0].authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$[0].message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$[0].created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).getUsersComments(userId);
    }

    @Test
    @SneakyThrows
    void deleteByIdByUser() {
        mvc.perform(delete("/users/{userId}/comments/{commentId}", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteByIdByUser(userId, commentId);
    }

}