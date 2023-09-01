package isthatkirill.main.user.service;

import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.user.dto.UserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql"})
    void createTest() {
        UserDto userDto = UserDto.builder()
                .email("email@email.ru")
                .name("name")
                .build();

        userDto = userService.create(userDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "email@email.ru");
    }

    @Test
    @Order(2)
    void createWithSameEmailTest() {
        UserDto userDto = UserDto.builder()
                .email("email@email.ru")
                .name("name")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(userDto));
    }

    @Test
    @Order(3)
    @Sql(value = "/testdata/test-users.sql")
    void getUsersTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        int from = 0;
        int size = 2;

        List<UserDto> users = userService.getUsers(ids, from, size);

        assertThat(users)
                .hasSize(2)
                .extracting(UserDto::getEmail)
                .containsExactly("email@email.ru", "email1@test.data");
    }

    @Test
    @Order(4)
    void getUsersWithEmptyListTest() {
        List<Long> ids = Collections.emptyList();
        int from = 0;
        int size = 10;

        List<UserDto> users = userService.getUsers(ids, from, size);

        assertThat(users)
                .hasSize(7);
    }

    @Test
    @Order(5)
    void deleteTest() {
        assertDoesNotThrow(() -> userService.delete(3L));
    }

    @Test
    @Order(6)
    void deleteNonExistentUserTest() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete(1000L));
    }
}