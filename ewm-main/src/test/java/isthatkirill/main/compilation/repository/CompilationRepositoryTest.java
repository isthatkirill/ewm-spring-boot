package isthatkirill.main.compilation.repository;

import isthatkirill.main.compilation.model.Compilation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-compilations.sql"})
class CompilationRepositoryTest {

    @Autowired
    private CompilationRepository compilationRepository;

    @Test
    void findAllByPinnedIsTrueTest() {
        Boolean pinned = true;
        Integer from = 0;
        Integer size = 10;

        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, PageRequest.of(from / size, size));

        assertThat(compilations).hasSize(3)
                .extracting(Compilation::getTitle)
                .containsExactlyInAnyOrder("high-level pl compilation", "low-level pl compilation", "spring compilation");
    }

    @Test
    void findAllByPinnedIsFalseTest() {
        Boolean pinned = false;
        Integer from = 0;
        Integer size = 10;

        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, PageRequest.of(from / size, size));

        assertThat(compilations).hasSize(4)
                .extracting(Compilation::getTitle)
                .containsExactlyInAnyOrder("frontend compilation", "backend compilation",
                        "web compilation", "empty compilation");
    }

    @Test
    void findAllByPinnedIsFalseWithPaginationTest() {
        Boolean pinned = false;
        Integer from = 1;
        Integer size = 2;

        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, PageRequest.of(from / size, size));

        assertThat(compilations).hasSize(2)
                .extracting(Compilation::getTitle)
                .containsExactlyInAnyOrder("frontend compilation", "backend compilation");
    }

}