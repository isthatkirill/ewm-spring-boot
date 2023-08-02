package ru.practicum.statServer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import ru.practicum.statClient.StatClient;
import ru.practicum.statClient.WebClientConfig;
import ru.practicum.statDto.dto.EndpointHitDto;
import ru.practicum.statDto.dto.ViewStatsDto;
import ru.practicum.statServer.StatServerApp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.statDto.util.Constants.FORMATTER;


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
        mockServer = startClientAndServer(9090);
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

        ResponseEntity<Object> response = statClient.getStats(start, end, List.of("/uri"), false);
        List<ViewStatsDto> stats = Arrays.asList(objectMapper.readValue(
                objectMapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));


        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(stats).hasSize(1);
    }

}
