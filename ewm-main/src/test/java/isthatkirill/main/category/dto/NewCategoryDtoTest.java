package isthatkirill.main.category.dto;

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
class NewCategoryDtoTest {

    @Autowired
    private JacksonTester<NewCategoryDto> json;

    private NewCategoryDto categoryDto;

    @Test
    @Order(1)
    @SneakyThrows
    void newCategoryDtoTest() {
        categoryDto = NewCategoryDto.builder()
                .name("name")
                .build();

        JsonContent<NewCategoryDto> result = json.write(categoryDto);

        assertThat(result)
                .hasJsonPathStringValue("$.name", categoryDto.getName());
    }

    @Test
    @Order(2)
    @SneakyThrows
    void newCategoryDtoWithNullFieldsTest() {
        categoryDto = NewCategoryDto.builder()
                .name(null)
                .build();

        JsonContent<NewCategoryDto> result = json.write(categoryDto);

        assertThat(result)
                .hasEmptyJsonPathValue("$.name");
    }

}