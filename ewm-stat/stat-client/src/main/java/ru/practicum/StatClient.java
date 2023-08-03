package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.Constants.FORMATTER;

@Component
@RequiredArgsConstructor
public class StatClient {

    private final WebClient webclient;

    public void addHit(EndpointHitDto endpointHitDto) {
        webclient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchangeToMono(clientResponse -> clientResponse.statusCode().equals(HttpStatus.CREATED) ?
                        clientResponse.bodyToMono(Object.class).map(body ->
                                ResponseEntity.status(HttpStatus.CREATED).body(body)) :
                        clientResponse.createException().flatMap(Mono::error))
                .block();
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webclient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .exchangeToMono(clientResponse -> {
            if (clientResponse.statusCode().is2xxSuccessful()) {
                return clientResponse.toEntity(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                        .map(responseEntity -> ResponseEntity.status(HttpStatus.CREATED).body(responseEntity.getBody()));
            } else {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new RuntimeException(errorMessage)));
            }
        })
                .block();
    }

}
