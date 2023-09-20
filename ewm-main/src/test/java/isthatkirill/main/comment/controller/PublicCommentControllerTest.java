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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = PublicCommentController.class)
class PublicCommentControllerTest {

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
    private final Long eventId = 1L;

    @Test
    @SneakyThrows
    void getAllCommentsForEventTest() {
        String keyword = "keyword";
        Integer from = 1;
        Integer size = 2;

        when(commentService.getAllCommentsForEvent(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseCommentDto));

        mvc.perform(get("/comments/{eventId}", eventId)
                        .param("keyword", keyword)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$[0].authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$[0].message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$[0].created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).getAllCommentsForEvent(eventId, keyword, from, size);
    }

    @Test
    @SneakyThrows
    void getAllCommentsForEventWithoutParamsTest() {
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        when(commentService.getAllCommentsForEvent(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(responseCommentDto));

        mvc.perform(get("/comments/{eventId}", eventId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(responseCommentDto.getId()))
                .andExpect(jsonPath("$[0].authorId").value(responseCommentDto.getAuthorId()))
                .andExpect(jsonPath("$[0].message").value(responseCommentDto.getMessage()))
                .andExpect(jsonPath("$[0].created").value(responseCommentDto.getCreated().format(formatter)));

        verify(commentService, times(1)).getAllCommentsForEvent(eventId, null, defaultFrom, defaultSize);
    }

    @Test
    @SneakyThrows
    void getAllCommentsForEventWithInvalidFromParamTest() {
        Integer from = -1;

        when(commentService.getAllCommentsForEvent(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(responseCommentDto));

        mvc.perform(get("/comments/{eventId}", eventId)
                        .param("from", String.valueOf(from))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).getAllCommentsForEvent(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllCommentsForEventWithInvalidSizeParamTest() {
        Integer size = 0;

        when(commentService.getAllCommentsForEvent(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(responseCommentDto));

        mvc.perform(get("/comments/{eventId}", eventId)
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(commentService, never()).getAllCommentsForEvent(anyLong(), anyString(), anyInt(), anyInt());
    }

}