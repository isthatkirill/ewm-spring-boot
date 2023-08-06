package ru.practicum.stats.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(value = {"/testdata/before_test_class.sql", "/schema.sql", "/testdata/add_more_hits.sql"})
class StatsRepositoryTest {

    @Autowired
    private StatsRepository statsRepository;

    @Test
    void getStatsTest() {
        List<ViewStatsDto> stats = statsRepository
                .getStats(LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));

        assertThat(stats).hasSize(2)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(3L, 3L);
    }

    @Test
    void getStatsUniqueIpTest() {
        List<ViewStatsDto> stats = statsRepository.getStatsUniqueIp(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1));

        assertThat(stats).hasSize(2)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(2L, 2L);
    }

    @Test
    void getStatsForUrisTest() {
        List<ViewStatsDto> stats = statsRepository.getStatsForUris(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1),
                List.of("/test"));

        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(3L);
    }

    @Test
    void getStatsUniqueIpForUrisTest() {
        List<ViewStatsDto> stats = statsRepository.getStatsUniqueIpForUris(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1),
                List.of("/test"));

        assertThat(stats).hasSize(1)
                .extracting(ViewStatsDto::getHits)
                .containsExactly(2L);
    }
}