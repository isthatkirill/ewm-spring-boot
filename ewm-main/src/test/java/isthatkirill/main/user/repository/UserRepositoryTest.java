package isthatkirill.main.user.repository;

import isthatkirill.main.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-users.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByIdInTest() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);

        int from = 0;
        int size = 3;

        List<User> users = userRepository.findAllByIdIn(ids, PageRequest.of(from / size, size));

        assertThat(users).hasSize(3)
                .extracting(User::getId)
                .containsExactly(1L, 2L, 3L);
    }
}