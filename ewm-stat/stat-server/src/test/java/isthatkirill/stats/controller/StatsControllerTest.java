package isthatkirill.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.stats.dto.EndpointHitDto;
import isthatkirill.stats.dto.ViewStatsDto;
import isthatkirill.stats.service.StatsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
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

import static isthatkirill.stats.util.Formats.DATE_PATTERN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    private EndpointHitDto endpointHitDto;
    private final ViewStatsDto viewStatsDto = ViewStatsDto.builder()
            .app("app")
            .hits(5L)
            .uri("/uri").build();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    @BeforeEach
    void buildHit() {
        endpointHitDto = EndpointHitDto.builder()
                .uri("/uri")
                .app("app")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    void addHitTest() {
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void addInvalidHitTest() {
        endpointHitDto.setUri(null);
        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    @SneakyThrows
    void getStatsTest() {
        when(statsService.getStats(any(), any(), anyList(), anyBoolean()))
                .thenReturn(List.of(viewStatsDto));

        mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("start", LocalDateTime.now().format(formatter))
                        .param("end", LocalDateTime.now().format(formatter))
                        .param("uris", List.of("/uri").toString())
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(viewStatsDto.getApp()))
                .andExpect(jsonPath("$[0].hits").value(viewStatsDto.getHits()))
                .andExpect(jsonPath("$[0].uri").value(viewStatsDto.getUri()));
    }

    @Test
    @SneakyThrows
    void getWithMissingRequiredParamTest() {
        mvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("end", LocalDateTime.now().format(formatter))
                        .param("uris", List.of("/uri").toString())
                        .param("unique", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing request parameter"));
    }
}