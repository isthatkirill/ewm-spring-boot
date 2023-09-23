package ru.practicum.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.stats.StatClient;
import isthatkirill.stats.WebClientConfig;
import isthatkirill.stats.dto.EndpointHitDto;
import isthatkirill.stats.dto.ViewStatsDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static isthatkirill.stats.util.Formats.DATE_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(classes = {StatClient.class, WebClientConfig.class})
class StatClientTest {

    @Autowired
    private StatClient statClient;

    private ClientAndServer mockServer;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
            .uri("/uri")
            .app("app")
            .ip("1.1.1.1")
            .timestamp(LocalDateTime.now())
            .build();
    private final ViewStatsDto viewStatsDto = ViewStatsDto.builder()
            .uri("/uri")
            .app("app")
            .hits(1L)
            .build();

    @BeforeEach
    void beforeEach() {
        mockServer = startClientAndServer(7070);
    }

    @AfterEach
    void afterEach() {
        mockServer.stop();
    }

    @Test
    @SneakyThrows
    void addHitViaClientTest() {
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/hit")
                                .withBody(objectMapper.writeValueAsString(endpointHitDto))
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        assertDoesNotThrow(() ->
                statClient.addHit(endpointHitDto)
        );
    }

    @Test
    @SneakyThrows
    void getStatsViaClientTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);

        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", start.format(formatter))
                                .withQueryStringParameter("uris", "/uri")
                                .withQueryStringParameter("end", end.format(formatter))
                                .withQueryStringParameter("unique", "false")
                )
                .respond(
                        response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(objectMapper.writeValueAsString(List.of(viewStatsDto)))
                                .withStatusCode(200)
                );

        List<ViewStatsDto> stats = statClient.getStats(start, end, List.of("/uri"), false);

        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getApp)
                .containsExactly("app");
    }

}