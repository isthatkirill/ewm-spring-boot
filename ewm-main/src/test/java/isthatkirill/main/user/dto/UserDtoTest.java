package isthatkirill.main.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private final UserDto userDto = UserDto.builder()
            .email("email@email.ru")
            .name("name")
            .id(1L)
            .build();

    @Test
    @Order(1)
    @SneakyThrows
    void userDtoTest() {
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result)
                .hasJsonPathStringValue("$.email", userDto.getEmail())
                .hasJsonPathStringValue("$.name", userDto.getName())
                .hasJsonPathNumberValue("$.id", userDto.getId());
    }

    @Test
    @Order(2)
    @SneakyThrows
    void userDtoWithNullFieldsTest() {
        userDto.setEmail(null);
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result)
                .hasJsonPathStringValue("$.name", userDto.getName())
                .hasEmptyJsonPathValue("$.email")
                .hasJsonPathNumberValue("$.id", userDto.getId());
    }

}