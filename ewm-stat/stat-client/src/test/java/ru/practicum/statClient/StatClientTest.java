package ru.practicum.statClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.StatClient;
import ru.practicum.WebClientConfig;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static ru.practicum.util.Constants.FORMATTER;

@SpringBootTest(classes = {StatClient.class, WebClientConfig.class})
class StatClientTest {

    @Autowired
    private StatClient statClient;

    private ClientAndServer mockServer;
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
                                .withQueryStringParameter("start", start.format(FORMATTER))
                                .withQueryStringParameter("uris", "/uri")
                                .withQueryStringParameter("end", end.format(FORMATTER))
                                .withQueryStringParameter("unique", "false")
                )
                .respond(
                        response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(objectMapper.writeValueAsString(List.of(viewStatsDto)))
                                .withStatusCode(200)
                );

        ResponseEntity<List<ViewStatsDto>> response = statClient.getStats(start, end, List.of("/uri"), false);
        List<ViewStatsDto> stats = response.getBody();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getApp)
                .containsExactly("app");
    }

}