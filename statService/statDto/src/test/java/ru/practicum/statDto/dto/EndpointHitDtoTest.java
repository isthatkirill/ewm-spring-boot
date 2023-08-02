package ru.practicum.statDto.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.statDto.util.Constants.FORMATTER;

@JsonTest
class EndpointHitDtoTest {

    @Autowired
    JacksonTester<EndpointHitDto> json;

    private final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
            .id(1L)
            .ip("1.1.1.1")
            .timestamp(LocalDateTime.now())
            .app("app")
            .uri("/uri").build();

    @Test
    @SneakyThrows
    void endpointHitDtoTest() {
        JsonContent<EndpointHitDto> result = json.write(endpointHitDto);

        assertThat(result)
                .hasJsonPathNumberValue("id", endpointHitDto.getId())
                .hasJsonPathStringValue("ip", endpointHitDto.getIp())
                .hasJsonPathStringValue("app", endpointHitDto.getApp())
                .hasJsonPathStringValue("uri", endpointHitDto.getUri())
                .hasJsonPathStringValue("timestamp", endpointHitDto.getTimestamp().format(FORMATTER));
    }

}