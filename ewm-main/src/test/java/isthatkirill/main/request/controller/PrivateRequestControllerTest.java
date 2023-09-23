package isthatkirill.main.request.controller;

import isthatkirill.main.request.dto.ParticipationRequestDto;
import isthatkirill.main.request.model.RequestState;
import isthatkirill.main.request.service.RequestServiceImpl;
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

import static isthatkirill.main.util.Formats.DATE_PATTERN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = PrivateRequestController.class)
class PrivateRequestControllerTest {

    @MockBean
    private RequestServiceImpl requestService;

    @Autowired
    private MockMvc mvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final Long eventId = 1L;
    private final Long userId = 1L;

    private final ParticipationRequestDto requestDto = ParticipationRequestDto.builder()
            .id(1L)
            .requester(userId)
            .event(eventId)
            .status(RequestState.PENDING)
            .created(LocalDateTime.now())
            .build();

    @Test
    @SneakyThrows
    void createTest() {
        when(requestService.create(any(), any())).thenReturn(requestDto);

        mvc.perform(post("/users/{userId}/requests", userId)
                        .param("eventId", String.valueOf(eventId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.requester").value(requestDto.getRequester()))
                .andExpect(jsonPath("$.event").value(requestDto.getEvent()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(formatter)))
                .andExpect(jsonPath("$.status").value(requestDto.getStatus().name()));

        verify(requestService, times(1)).create(eventId, userId);
    }

    @Test
    @SneakyThrows
    void createWithMissingRequestParamTest() {
        mvc.perform(post("/users/{userId}/requests", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(requestService, never()).create(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestsByUserIdTest() {
        when(requestService.getRequestsByUserId(any())).thenReturn(List.of(requestDto));

        mvc.perform(get("/users/{userId}/requests", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].requester").value(requestDto.getRequester()))
                .andExpect(jsonPath("$[0].event").value(requestDto.getEvent()))
                .andExpect(jsonPath("$[0].created").value(requestDto.getCreated().format(formatter)))
                .andExpect(jsonPath("$[0].status").value(requestDto.getStatus().name()));

        verify(requestService, times(1)).getRequestsByUserId(userId);
    }

    @Test
    @SneakyThrows
    void cancelTest() {
        when(requestService.cancel(anyLong(), anyLong())).thenReturn(requestDto);

        mvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", userId, requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.requester").value(requestDto.getRequester()))
                .andExpect(jsonPath("$.event").value(requestDto.getEvent()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(formatter)))
                .andExpect(jsonPath("$.status").value(requestDto.getStatus().name()));

        verify(requestService, times(1)).cancel(userId, requestDto.getId());
    }

}