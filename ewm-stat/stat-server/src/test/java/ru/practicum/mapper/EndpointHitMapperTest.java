package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointHitMapperTest {

    private final EndpointHitMapper mapper = Mappers.getMapper(EndpointHitMapper.class);

    @Test
    void toEndpointHitTest() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .uri("/uri")
                .ip("1.1.1.1")
                .app("app")
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHit endpointHit = mapper.toEndpointHit(endpointHitDto);

        assertThat(endpointHit).isNotNull()
                .hasFieldOrPropertyWithValue("uri", endpointHitDto.getUri())
                .hasFieldOrPropertyWithValue("ip", endpointHitDto.getIp())
                .hasFieldOrPropertyWithValue("app", endpointHitDto.getApp())
                .hasFieldOrPropertyWithValue("timestamp", endpointHitDto.getTimestamp());
    }

}