package ru.practicum.stats.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class StatsServiceImplTest {

    @Autowired
    private StatsService statsService;

    @Test
    @Order(1)
    @Sql(value = {"/testdata/before_test_class.sql", "/schema.sql"})
    void addHitTest() {

        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .uri("/uri")
                .ip("1.1.1.1")
                .app("app")
                .timestamp(LocalDateTime.now())
                .build();

        assertDoesNotThrow(() -> {
            statsService.addHit(endpointHit);
        });
    }

    @Test
    @Order(2)
    @Sql(value = "/testdata/add_more_hits.sql")
    void addHitWithNullFieldTest() {

        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .ip("1.1.1.1")
                .app("app")
                .timestamp(LocalDateTime.now())
                .build();

        assertThrows(DataIntegrityViolationException.class, () ->
                statsService.addHit(endpointHit)
        );
    }

    @Test
    @Order(3)
    void getAllStatsTest() {
        List<ViewStatsDto> stats = statsService.getStats(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1), null, false);

        assertThat(stats).hasSize(3)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(3L, 3L, 1L);
    }

    @Test
    @Order(4)
    void getAllStatsForUriTest() {
        List<ViewStatsDto> stats = statsService.getStats(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1), List.of("/test"), false);

        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(3L);
    }

    @Test
    @Order(5)
    void getAllStatsForUriUniqueTest() {
        List<ViewStatsDto> stats = statsService.getStats(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1), List.of("/test"), true);

        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(2L);
    }

    @Test
    @Order(6)
    void getAllStatsUniqueTest() {
        List<ViewStatsDto> stats = statsService.getStats(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1), null, true);

        assertThat(stats).hasSize(3)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(2L, 2L, 1L);
    }

}
